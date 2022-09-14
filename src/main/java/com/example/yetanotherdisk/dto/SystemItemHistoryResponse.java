package com.example.yetanotherdisk.dto;

import lombok.Data;

import java.util.List;

@Data
public class SystemItemHistoryResponse {
    private List<SystemItemHistoryUnitResponse> items;
}
