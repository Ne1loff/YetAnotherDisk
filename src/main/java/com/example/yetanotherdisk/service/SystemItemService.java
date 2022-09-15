package com.example.yetanotherdisk.service;

import com.example.yetanotherdisk.dto.SystemItemImportRequest;
import com.example.yetanotherdisk.dto.SystemItemResponse;

import javax.validation.constraints.NotNull;

public interface SystemItemService {

    void importItems(@NotNull SystemItemImportRequest imports);

    SystemItemResponse getItem(@NotNull String id);

    void deleteItem(@NotNull String id, @NotNull String dateString);
}
