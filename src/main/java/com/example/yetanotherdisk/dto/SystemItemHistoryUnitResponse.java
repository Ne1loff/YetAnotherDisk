package com.example.yetanotherdisk.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemItemHistoryUnitResponse extends AbstractDto {
    private String url;
    private String parentId;
    private Long size;
}
