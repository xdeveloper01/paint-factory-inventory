import { IsUUID, IsOptional, IsArray, ValidateNested, IsNumber } from 'class-validator';
import { Type } from 'class-transformer';

export class ActualConsumptionDto {
  @IsUUID()
  rawMaterialId!: string;

  @IsNumber()
  actualQuantity!: number;
}

export class CompleteBatchDto {
  @IsUUID()
  batchId!: string;

  @IsUUID()
  productionLocationId!: string;

  // Optional: If provided, overrides the BOM target quantities with actual consumed quantities (e.g. spillages)
  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => ActualConsumptionDto)
  actualConsumptions?: ActualConsumptionDto[];
}
