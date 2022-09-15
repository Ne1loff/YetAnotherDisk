package com.example.yetanotherdisk.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemItemResponse extends AbstractDto {

    private String parentId;

    private String url;

    private Long size;

    private List<SystemItemResponse> children;
}
