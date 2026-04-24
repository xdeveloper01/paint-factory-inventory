import { Injectable, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateStockMovementDto } from './dto/create-stock-movement.dto';
import { MovementType, Prisma } from '@prisma/client';

@Injectable()
export class StockMovementService {
  constructor(private prisma: PrismaService) { }

  /**
   * Records a stock movement. Ensure atomicity using transactions.
   * Validates stock availability if movement is outbound.
   * Accepts an optional transaction client for composability.
   */
  async recordMovement(dto: CreateStockMovementDto, externalTx?: Prisma.TransactionClient) {
    const executeLogic = async (tx: Prisma.TransactionClient) => {
      // 1. If movement is an outbound type, quantity must be negative.
      // We will ensure quantity signs are correct based on movement type
      const isOutbound = ([
        MovementType.PRODUCTION_CONSUMPTION,
        MovementType.DISPATCH,
      ] as MovementType[]).includes(dto.movementType);

      const isTransfer = dto.movementType === MovementType.TRANSFER;

      let finalQuantity = new Prisma.Decimal(dto.quantity);

      if (isOutbound && finalQuantity.isPositive()) {
        finalQuantity = finalQuantity.negated();
      } else if (!isOutbound && !isTransfer && finalQuantity.isNegative()) {
        finalQuantity = finalQuantity.abs();
      }

      // 2. Validate current stock if it's an outbound movement to prevent negative stock
      if (finalQuantity.isNegative()) {
        const currentStock = await this.getCurrentStockTx(tx, dto.itemId, dto.locationId, dto.lotId);
        if (currentStock.plus(finalQuantity).isNegative()) {
          throw new BadRequestException(
            `Insufficient stock for item ${dto.itemId} at location ${dto.locationId}. Current: ${currentStock.toNumber()}`
          );
        }
      }

      // 3. Create movement
      const movement = await tx.stockMovement.create({
        data: {
          itemId: dto.itemId,
          locationId: dto.locationId,
          lotId: dto.lotId,
          quantity: finalQuantity,
          movementType: dto.movementType,
          referenceId: dto.referenceId,
          notes: dto.notes,
        },
      });

      return movement;
    };

    return externalTx ? executeLogic(externalTx) : this.prisma.$transaction(executeLogic);
  }

  /**
   * Helper to get current stock inside a transaction dynamically using SUM(quantity)
   */
  private async getCurrentStockTx(
    tx: Prisma.TransactionClient,
    itemId: string,
    locationId?: string,
    lotId?: string
  ): Promise<Prisma.Decimal> {
    const where: Prisma.StockMovementWhereInput = { itemId };

    if (locationId) {
      where.locationId = locationId;
    }

    if (lotId) {
      where.lotId = lotId;
    }

    const aggregation = await tx.stockMovement.aggregate({
      _sum: {
        quantity: true,
      },
      where,
    });

    return aggregation._sum.quantity || new Prisma.Decimal(0);
  }

  /**
   * Public method to get real-time stock balance for an item
   */
  async getCurrentStock(itemId: string, locationId?: string, lotId?: string): Promise<number> {
    const stock = await this.getCurrentStockTx(this.prisma, itemId, locationId, lotId);
    return stock.toNumber();
  }

  /**
   * Atomic stock transfer between locations
   */
  async transferStock(
    itemId: string,
    fromLocationId: string,
    toLocationId: string,
    quantity: number,
    referenceId?: string
  ) {
    if (quantity <= 0) {
      throw new BadRequestException('Transfer quantity must be positive');
    }

    return this.prisma.$transaction(async (tx) => {
      // Create OUT movement
      await this.recordMovement({
        itemId,
        locationId: fromLocationId,
        quantity: -quantity,
        movementType: MovementType.TRANSFER,
        referenceId,
        notes: `Transfer to ${toLocationId}`,
      });

      // Create IN movement
      await this.recordMovement({
        itemId,
        locationId: toLocationId,
        quantity: quantity,
        movementType: MovementType.TRANSFER,
        referenceId,
        notes: `Transfer from ${fromLocationId}`,
      });

      return { success: true, message: 'Stock transferred successfully' };
    });
  }
}
