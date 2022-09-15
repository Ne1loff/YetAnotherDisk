package com.example.yetanotherdisk.service;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.example.yetanotherdisk.dto.SystemItemHistoryResponse;
import com.example.yetanotherdisk.dto.SystemItemImport;
import com.example.yetanotherdisk.dto.SystemItemImportRequest;
import com.example.yetanotherdisk.dto.SystemItemResponse;
import com.example.yetanotherdisk.entity.SystemItem;
import com.example.yetanotherdisk.exception.badRequest.ValidationError;
import com.example.yetanotherdisk.exception.notFound.ItemNotFoundError;
import com.example.yetanotherdisk.mapper.EntityMapper;
import com.example.yetanotherdisk.mapper.SystemItemHistoryMapper;
import com.example.yetanotherdisk.mapper.SystemItemMapper;
import com.example.yetanotherdisk.repository.SystemItemHistoryRepository;
import com.example.yetanotherdisk.repository.SystemItemRepository;
import com.example.yetanotherdisk.validator.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SystemItemServiceImpl implements SystemItemService, SystemItemHistoryService {

    private final SystemItemHistoryRepository historyRepository;
    private final SystemItemRepository itemRepository;
    private final SystemItemMapper itemMapper;
    private final SystemItemHistoryMapper historyMapper;
    private final EntityMapper entityMapper;
    private final Validator validator;

    public SystemItemServiceImpl(SystemItemHistoryRepository historyRepository, SystemItemRepository itemRepository, SystemItemMapper itemMapper, SystemItemHistoryMapper historyMapper, EntityMapper entityMapper, Validator validator) {
        this.historyRepository = historyRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.historyMapper = historyMapper;
        this.entityMapper = entityMapper;
        this.validator = validator;
    }

    @Override
    @Transactional
    public void importItems(@NotNull SystemItemImportRequest request) {
        var updateTime = validator.validateDateThenGet(request.getUpdateDate());

        var importsIds = request.getItems().stream()
                .peek(validator::isValidImport)
                .map(SystemItemImport::getId)
                .collect(Collectors.toSet());

        if (importsIds.size() != request.getItems().size()) throw new ValidationError();

        var entities = request.getItems().stream()
                .map(itemMapper::fromImport)
                .peek((it) -> it.setDate(updateTime))
                .collect(Collectors.toMap(SystemItem::getId, (it) -> it));

        var parentIdsToFind = request.getItems().stream()
                .map(SystemItemImport::getParentId)
                .filter(Objects::nonNull)
                .filter((it) -> !entities.containsKey(it))
                .collect(Collectors.toSet());

        var foundItems = itemRepository.findSystemItemsByIdsAndTypeIsFolder(parentIdsToFind);
        if (foundItems.size() != parentIdsToFind.size()) throw new ValidationError();

        var itemSizeDiffs = new HashMap<String, Long>();

        var oldEntities = itemRepository.findSystemItemsByIds(importsIds)
                .stream()
                .collect(Collectors.toMap(SystemItem::getId, (it) -> it));


        var entitiesForWrite = new HashSet<SystemItem>();

        for (SystemItem entity : entities.values()) {
            if (oldEntities.containsKey(entity.getId())) {

                var oldEntity = oldEntities.get(entity.getId());

                if (entity.getType() != oldEntity.getType()) throw new ValidationError();

                var oldParentId = oldEntity.getParentId();
                var newParentId = entity.getParentId();

                var isFolder = entity.getType() == SystemItemType.FOLDER;

                if (isFolder) {
                    entity.setSize(oldEntity.getSize());
                }

                if (Objects.equals(oldParentId, newParentId)) {
                    itemSizeDiffs.compute(newParentId, (key, val) ->
                            (entity.getSize() - oldEntity.getSize()) + (val != null ? val : 0));

                } else {
                    if (oldParentId != null)
                        itemSizeDiffs.compute(oldParentId, (key, val) -> -oldEntity.getSize() + (val != null ? val : 0));

                    if (newParentId != null)
                        itemSizeDiffs.compute(newParentId, (key, val) -> entity.getSize() + (val != null ? val : 0));
                }

                if (isFolder) {
                    if (!Objects.equals(oldParentId, newParentId)) {
                        oldEntity.setParentId(entity.getParentId());
                    }
                    entitiesForWrite.add(oldEntity);
                } else {
                    entitiesForWrite.add(entity);
                }

            } else {
                var parentId = entity.getParentId();
                if (parentId != null) {
                    itemSizeDiffs.compute(parentId, (key, val) -> entity.getSize() + (val != null ? val : 0));
                }

                entitiesForWrite.add(entity);
            }
        }

        itemRepository.saveAllAndFlush(entitiesForWrite);

        var updatedItemsId = new HashSet<String>();

        itemSizeDiffs.entrySet().stream()
                .filter((entry) -> entry.getValue() != 0)
                .forEach((entry -> {
                    var idForUpdateSize = itemRepository.getParentsIdByItemId(entry.getKey());
                    itemRepository.updateSizeAndDateByIds(entry.getValue(), updateTime, idForUpdateSize);
                    updatedItemsId.addAll(idForUpdateSize);
                }));

        var updatedItems = itemRepository.findSystemItemsByIds(updatedItemsId);

        updatedItems.addAll(entitiesForWrite);

        var historyEntities = updatedItems.stream()
                .map(entityMapper::toHistoryEntity)
                .toList();

        historyRepository.saveAll(historyEntities);

    }

    @Override
    public SystemItemResponse getItem(@NotNull String id) {
        return itemMapper.toDto(itemRepository.findSystemItemById(id).orElseThrow(ItemNotFoundError::new));
    }

    @Override
    @Transactional
    public void deleteItem(@NotNull String id, @NotNull String dateString) {

        var date = validator.validateDateThenGet(dateString);

        var item = itemRepository.findSystemItemById(id)
                .orElseThrow(ItemNotFoundError::new);

        var parentsId = itemRepository.getParentsIdByItemId(id);
        itemRepository.updateSizeAndDateByIds(-item.getSize(), date, parentsId);

        var historyUnits = itemRepository.findSystemItemsByIds(parentsId).stream()
                .map(entityMapper::toHistoryEntity)
                .toList();

        historyRepository.saveAll(historyUnits);

        var childrenId = itemRepository.getChildrenIdByItemId(id);
        historyRepository.markAsDeleted(childrenId);

        //itemRepository.deleteById(id);
        itemRepository.deleteAllByIdInBatch(childrenId);
    }

    @Override
    public SystemItemHistoryResponse getAllHistoryForLastDay(String endDateString) {
        var endDate = validator.validateDateThenGet(endDateString);
        var startDate = endDate.minusHours(24);

        var historyItems = historyRepository
                .getSystemItemHistoryUnitsByTypeAndIsNotDeletedAndDateIsBetween(
                        SystemItemType.FILE, startDate, endDate);

        var historyUnits = historyItems.stream()
                .map(historyMapper::toDto)
                .toList();

        return new SystemItemHistoryResponse(historyUnits);
    }

    @Override
    public SystemItemHistoryResponse getItemHistoryBetween(
            @NotNull String id,
            @NotNull String startDateString,
            @NotNull String endDateString
    ) {
        var startDate = validator.validateDateThenGet(startDateString);
        var endDate = validator.validateDateThenGet(endDateString);

        if (!itemRepository.existsById(id)) throw new ItemNotFoundError();

        var historyItems = historyRepository
                .getItemHistoriesByShopUnitIdAndIsNotDeletedAndDateBetween(id, startDate, endDate);

        var historyUnits = historyItems.stream()
                .map(historyMapper::toDto)
                .toList();

        return new SystemItemHistoryResponse(historyUnits);
    }
}
