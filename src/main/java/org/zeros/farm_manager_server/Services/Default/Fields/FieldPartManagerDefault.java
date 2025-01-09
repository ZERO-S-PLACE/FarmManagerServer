package org.zeros.farm_manager_server.Services.Default.Fields;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.Fields.FieldPartDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class FieldPartManagerDefault implements FieldPartManager {

    private final FieldRepository fieldRepository;
    private final FieldPartRepository fieldPartRepository;
    private final EntityManager entityManager;
    private final FieldManager fieldManager;


    @Override
    public void divideFieldPart(UUID originPartId, FieldPartDTO part1DTO, FieldPartDTO part2DTO) {
        FieldPart originPart = fieldPartRepository.findById(originPartId).orElse(FieldPart.NONE);
        if (originPart.equals(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        if (part1DTO.getArea().floatValue() >= originPart.getArea().floatValue()) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.NOT_COMPATIBLE);
        }
        Field field = originPart.getField();
        FieldPart part1 = rewriteToEntity(part1DTO, FieldPart.NONE);
        FieldPart part2 = rewriteToEntity(part1DTO, FieldPart.NONE);
        part2.setArea(originPart.getArea().subtract(part1.getArea()));
        part1.setField(field);
        part2.setField(field);
        originPart.setIsArchived(true);
        FieldPart part1Saved = fieldPartRepository.saveAndFlush(part1);
        FieldPart part2Saved = fieldPartRepository.saveAndFlush(part2);
        field.getFieldParts().add(part1Saved);
        field.getFieldParts().add(part2Saved);
        flushChanges();
    }

    private FieldPart rewriteToEntity(FieldPartDTO dto, FieldPart entity) {
        FieldPart parsedEntity = DefaultMappers.fieldPartMapper.dtoToEntitySimpleProperties(dto);
        parsedEntity.setField(entity.getField());
        parsedEntity.setCrops(entity.getCrops());
        parsedEntity.setCreatedDate(entity.getCreatedDate());
        parsedEntity.setLastModifiedDate(entity.getLastModifiedDate());
        parsedEntity.setCreatedDate(entity.getCreatedDate());
        return parsedEntity;
    }

    @Override
    public FieldPart mergeFieldParts(Set<UUID> fieldPartsIds) {
        Set<FieldPart> fieldParts = fieldPartsIds.stream().map(this::getFieldPartById).collect(Collectors.toSet());
        if (fieldParts.size() < 2) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.NOT_COMPATIBLE);
        }
        if (fieldParts.contains(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }

        FieldPart firstPart = fieldParts.stream().findFirst().orElse(FieldPart.NONE);
        fieldParts.remove(firstPart);
        FieldPart merged = FieldPart.builder().fieldPartName(firstPart.getFieldPartName()).area(firstPart.getArea()).field(firstPart.getField()).build();
        BigDecimal targetArea = merged.getArea();
        for (FieldPart fieldPart : fieldParts) {
            targetArea = targetArea.add(fieldPart.getArea());
            fieldPart.setIsArchived(true);
        }
        merged.setFieldPartName(validateNewFieldPartName(merged.getFieldPartName() + "_merged", firstPart.getField().getId()));
        merged.setArea(targetArea);
        merged.setId(null);
        merged.setIsArchived(false);
        firstPart.setIsArchived(true);
        FieldPart mergedSaved = fieldPartRepository.saveAndFlush(merged);
        flushChanges();
        return mergedSaved;
    }

    @Override
    public Set<FieldPart> getAllFieldParts(UUID fieldId) {
        return fieldManager.getFieldById(fieldId).getFieldParts();
    }

    @Override
    public Set<FieldPart> getAllNonArchivedFieldParts(UUID fieldId) {
        return new HashSet<>(fieldPartRepository.findAllByFieldAndIsArchived(fieldManager.getFieldById(fieldId), false));
    }

    @Override
    public FieldPart updateFieldPartName(UUID fieldPartId, String newName) {
        FieldPart fieldPart = getFieldPartIfExist(fieldPartId);
        if (newName.equals(fieldPart.getFieldPartName())) {
            return fieldPart;
        }
        fieldPart.setFieldPartName(validateNewFieldPartName(newName, fieldPart.getField().getId()));
        flushChanges();
        return getFieldPartById(fieldPartId);
    }

    private FieldPart getFieldPartIfExist(UUID fieldPartId) {
        if (fieldPartId == null) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        FieldPart fieldPart = getFieldPartById(fieldPartId);
        if (fieldPart.equals(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return fieldPart;
    }

    @Override
    public void updateFieldPartAreaResizeField(UUID fieldPartId, BigDecimal newArea) {
        FieldPart fieldPart = getFieldPartIfExist(fieldPartId);
        Field field = fieldPart.getField();
        field.setArea(field.getArea().subtract(fieldPart.getArea()).add(newArea));
        fieldPart.setArea(newArea);
        flushChanges();

    }

    @Override
    public void updateFieldPartAreaTransfer(UUID basePartId, UUID resizedPartId, BigDecimal newArea) {
        FieldPart basePart = getFieldPartIfExist(basePartId);
        FieldPart resizedPart = getFieldPartIfExist(resizedPartId);
        Field field = fieldRepository.findById(basePart.getField().getId()).orElse(Field.NONE);
        if (basePart.equals(FieldPart.NONE)
                || field.equals(Field.NONE)
                || resizedPart.equals(FieldPart.NONE)) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        resizedPart.setArea(resizedPart.getArea().subtract(basePart.getArea()).add(newArea));
        basePart.setArea(newArea);
        flushChanges();
    }

    @Override
    public FieldPart getFieldPartById(UUID id) {
        return fieldPartRepository.findById(id).orElse(FieldPart.NONE);
    }


    private String validateNewFieldPartName(String name, UUID fieldId) {
        Field field = fieldManager.getFieldById(fieldId);
        if (field.equals(Field.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    FieldPart.class, Set.of("field"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (name.isBlank()) {
            name = "NewPart" + fieldPartRepository.findAllByField(field).size();
        }
        boolean nameUpdated;
        do {
            nameUpdated = false;
            for (FieldPart fieldPart : field.getFieldParts()) {
                if (fieldPart.getFieldPartName().equals(name)) {
                    name = name + "_1";
                    nameUpdated = true;
                }
            }
        } while (nameUpdated);
        return name;
    }


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }
}
