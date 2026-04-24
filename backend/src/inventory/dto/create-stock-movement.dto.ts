import { IsUUID, IsNumber, IsEnum, IsOptional, IsString, Min } from 'class-validator';
import { MovementType } from '@prisma/client';

export class CreateStockMovementDto {
  @IsUUID()
  itemId!: string;

  @IsUUID()
  locationId!: string;

  @IsOptional()
  @IsUUID()
  lotId?: string;

  @IsNumber()
  quantity!: number;

  @IsEnum(MovementType)
  movementType!: MovementType;

  @IsOptional()
  @IsUUID()
  referenceId?: string;

  @IsOptional()
  @IsString()
  notes?: string;
}
