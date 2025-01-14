package org.zeros.farm_manager_server.Services.Default.Fields;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
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
    @Transactional
    public FieldPartDTO mergeFieldParts(Set<UUID> fieldPartsIds) {
        Set<FieldPart> fieldParts = fieldPartsIds.stream().map(this::getFieldPartIfExists).collect(Collectors.toSet());
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
        return getFieldPartById(mergedSaved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<FieldPartDTO> getAllFieldParts(UUID fieldId) {
        return fieldManager.getFieldById(fieldId).getFieldParts().stream().map(this::getFieldPartById).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<FieldPartDTO> getAllNonArchivedFieldParts(UUID fieldId) {
        return fieldPartRepository.findAllByFieldAndIsArchived(fieldManager.getFieldIfExists(fieldId), false).stream().map(
                DefaultMappers.fieldPartMapper::entityToDto).collect(Collectors.toSet());

    }

    @Override
    @Transactional
    public FieldPartDTO updateFieldPartName(UUID fieldPartId, String newName) {
        FieldPart fieldPart = getFieldPartIfExists(fieldPartId);
        if (!newName.equals(fieldPart.getFieldPartName())) {
            fieldPart.setFieldPartName(validateNewFieldPartName(newName, fieldPart.getField().getId()));
            flushChanges();
        }
        return getFieldPartById(fieldPartId);
    }

    @Override
    @Transactional
    public void updateFieldPartAreaResizeField(UUID fieldPartId, BigDecimal newArea) {
        FieldPart fieldPart = getFieldPartIfExists(fieldPartId);
        Field field = fieldPart.getField();
        field.setArea(field.getArea().subtract(fieldPart.getArea()).add(newArea));
        fieldPart.setArea(newArea);
        flushChanges();

    }

    @Override
    @Transactional
    public void updateFieldPartAreaTransfer(UUID basePartId, UUID resizedPartId, BigDecimal newArea) {
        FieldPart basePart = getFieldPartIfExists(basePartId);
        FieldPart resizedPart = getFieldPartIfExists(resizedPartId);
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
    public FieldPartDTO getFieldPartById(UUID id) {
        FieldPart fieldPart = fieldPartRepository.findById(id).orElseThrow(() -> new
                IllegalArgumentExceptionCustom(FieldPart.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.fieldPartMapper.entityToDto(fieldPart);
    }


    private String validateNewFieldPartName(String name, UUID fieldId) {
        Field field = fieldManager.getFieldIfExists(fieldId);
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

    @Override
    @Transactional(readOnly = true)
    public FieldPart getFieldPartIfExists(UUID fieldPartId) {
        if (fieldPartId == null) {
            throw new IllegalArgumentExceptionCustom(FieldPart.class, Set.of("field"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }

        return fieldPartRepository.findById(fieldPartId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(FieldPart.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }

}
