import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { StockMovementService } from '../inventory/stock-movement.service';
import { CompleteBatchDto } from './dto/complete-batch.dto';
import { BatchStatus, MovementType } from '@prisma/client';

@Injectable()
export class ProductionService {
  constructor(
    private prisma: PrismaService,
    private stockMovementService: StockMovementService,
  ) {}

  /**
   * Completes a production batch atomically.
   * Deducts raw materials and adds finished goods in a single transaction.
   */
  async completeBatch(dto: CompleteBatchDto) {
    return this.prisma.$transaction(async (tx) => {
      // 1. Fetch batch and BOM
      const batch = await tx.productionBatch.findUnique({
        where: { id: dto.batchId },
        include: {
          bom: {
            include: { components: true },
          },
        },
      });

      if (!batch) {
        throw new NotFoundException(`Batch with ID ${dto.batchId} not found`);
      }

      if (batch.status === BatchStatus.COMPLETED) {
        throw new BadRequestException('Batch is already completed');
      }

      // 2. Determine raw material consumptions
      const consumptions = new Map<string, number>();

      // Default to BOM targets based on ratio (if yield differs from BOM base, we'd scale it,
      // but assuming BOM is for 1 unit of finished good for simplicity, or we just use targetQuantity directly).
      // Here we assume BOM component targetQuantity is for the whole batch targetYield.
      for (const component of batch.bom.components) {
        consumptions.set(component.rawMaterialId, component.targetQuantity.toNumber());
      }

      // Override with actuals if provided
      if (dto.actualConsumptions && dto.actualConsumptions.length > 0) {
        for (const actual of dto.actualConsumptions) {
          if (!consumptions.has(actual.rawMaterialId)) {
            throw new BadRequestException(`Material ${actual.rawMaterialId} is not in the BOM for this batch`);
          }
          consumptions.set(actual.rawMaterialId, actual.actualQuantity);
        }
      }

      // 3. Record OUT movements for all consumed raw materials
      for (const [rawMaterialId, quantity] of consumptions.entries()) {
        await this.stockMovementService.recordMovement(
          {
            itemId: rawMaterialId,
            locationId: dto.productionLocationId,
            quantity: quantity,
            movementType: MovementType.PRODUCTION_CONSUMPTION,
            referenceId: batch.id,
            notes: `Consumed for batch ${batch.batchNumber}`,
          },
          tx
        );
      }

      // 4. Record IN movement for finished good
      await this.stockMovementService.recordMovement(
        {
          itemId: batch.finishedGoodId,
          locationId: dto.productionLocationId,
          quantity: batch.targetYield.toNumber(),
          movementType: MovementType.PRODUCTION_YIELD,
          referenceId: batch.id,
          notes: `Yield from batch ${batch.batchNumber}`,
        },
        tx
      );

      // 5. Update Batch Status
      const updatedBatch = await tx.productionBatch.update({
        where: { id: batch.id },
        data: {
          status: BatchStatus.COMPLETED,
          updatedAt: new Date(),
        },
      });

      return updatedBatch;
    });
  }
}
