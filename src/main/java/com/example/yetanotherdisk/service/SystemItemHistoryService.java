package com.example.yetanotherdisk.service;

import com.example.yetanotherdisk.dto.SystemItemHistoryResponse;

import javax.validation.constraints.NotNull;

public interface SystemItemHistoryService {

    SystemItemHistoryResponse getAllHistoryForLastDay(@NotNull String startDateString);

    SystemItemHistoryResponse getItemHistoryBetween(@NotNull String id, @NotNull String startDateString, @NotNull String endDateString);
}
