package org.zeros.farm_manager_server.Services.Interface.Fields;

import jakarta.validation.constraints.NotNull;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public interface FieldPartManager {

    void divideFieldPart(@NotNull UUID originPartId, @NotNull FieldPartDTO part1DTO, @NotNull FieldPartDTO part2DTO);

    FieldPart mergeFieldParts(@NotNull Set<UUID> fieldPartsIds);

    Set<FieldPart> getAllFieldParts(@NotNull UUID fieldId);

    Set<FieldPart> getAllNonArchivedFieldParts(@NotNull UUID fieldId);

    FieldPart updateFieldPartName(@NotNull UUID fieldPartId, @NotNull String newName);

    void updateFieldPartAreaResizeField(@NotNull UUID fieldPartId, @NotNull BigDecimal newArea);

    void updateFieldPartAreaTransfer(@NotNull UUID changedPartId, @NotNull UUID resizedPartId, @NotNull BigDecimal newArea);

    FieldPart getFieldPartById(@NotNull UUID id);


}
