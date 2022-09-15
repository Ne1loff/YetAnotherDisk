package com.example.yetanotherdisk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SystemItemHistoryResponse {
    private List<SystemItemHistoryUnitResponse> items;
}
