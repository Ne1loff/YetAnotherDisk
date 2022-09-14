package com.example.yetanotherdisk.service;

import com.example.yetanotherdisk.dto.SystemItemImport;
import com.example.yetanotherdisk.dto.SystemItemImportRequest;
import com.example.yetanotherdisk.dto.SystemItemResponse;
import com.example.yetanotherdisk.entity.SystemItem;
import com.example.yetanotherdisk.exception.notFound.ItemNotFoundError;
import com.example.yetanotherdisk.mapper.EntityMapper;
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
public class SystemItemServiceImpl implements SystemItemService {

    private final SystemItemHistoryRepository historyRepository;
    private final SystemItemRepository itemRepository;
    private final SystemItemMapper mapper;
    private final EntityMapper entityMapper;
    private final Validator validator;

    public SystemItemServiceImpl(SystemItemHistoryRepository historyRepository, SystemItemRepository itemRepository, SystemItemMapper mapper, EntityMapper entityMapper, Validator validator) {
        this.historyRepository = historyRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.entityMapper = entityMapper;
        this.validator = validator;
    }

    @Transactional
    public void importSystemItems(@NotNull SystemItemImportRequest request) {
        var updateTime = validator.validateDateThenGet(request.getUpdateDate());

        var importsIds = request.getItems().stream()
                .peek(validator::isValidImport)
                .map(SystemItemImport::getId)
                .collect(Collectors.toSet());

        var itemSizeDiffs = new HashMap<String, Long>();

        var oldEntities = itemRepository.findSystemItemsByIds(importsIds)
                .stream()
                .collect(Collectors.toMap(SystemItem::getId, (it) -> it));

        var entities = request.getItems().stream()
                .map(mapper::fromImport)
                .peek((it) -> it.setDate(updateTime))
                .collect(Collectors.toMap(SystemItem::getId, (it) -> it));

        for (SystemItem entity : entities.values()) {
            if (oldEntities.containsKey(entity.getId())) {
                var oldEntity = oldEntities.get(entity.getId());

                var oldParentId = oldEntity.getParentId();
                var newParentId = entity.getParentId();

                if (Objects.equals(oldParentId, newParentId)) {
                    itemSizeDiffs.compute(newParentId, (key, val) ->
                            (entity.getSize() - oldEntity.getSize()) + (val != null ? val : 0));

                } else {
                    itemSizeDiffs.compute(oldParentId, (key, val) -> -oldEntity.getSize() + (val != null ? val : 0));
                    itemSizeDiffs.compute(newParentId, (key, val) -> entity.getSize() + (val != null ? val : 0));
                }
            } else {
                var parentId = entity.getParentId();
                if (parentId != null) {
                    itemSizeDiffs.compute(parentId, (key, val) -> entity.getSize() + (val != null ? val : 0));
                }
            }
        }

        itemRepository.saveAllAndFlush(entities.values());

        var updatedItemsId = new HashSet<String>();

        itemSizeDiffs.entrySet().stream()
                .filter((entry) -> entry.getValue() != 0)
                .forEach((entry -> {
                    var idForUpdateSize = itemRepository.getParentsIdByItemId(entry.getKey());
                    itemRepository.updateSizeAndDateById(entry.getValue(), updateTime, idForUpdateSize);
                    updatedItemsId.addAll(idForUpdateSize);
                }));

        var updatedItems = itemRepository.findSystemItemsByIds(updatedItemsId);

        updatedItems.addAll(entities.values());

        var historyEntities = updatedItems.stream()
                .map(entityMapper::toHistoryEntity)
                .toList();

        historyRepository.saveAll(historyEntities);

    }


    public SystemItemResponse getItem(@NotNull String id) {
        return mapper.toDto(itemRepository.findSystemItemById(id).orElseThrow(ItemNotFoundError::new));
    }

    @Override
    public void deleteItem(String id) {

    }
}
