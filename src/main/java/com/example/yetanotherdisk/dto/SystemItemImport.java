package com.example.yetanotherdisk.dto;

import com.example.yetanotherdisk.attributes.SystemItemType;
import lombok.Data;

@Data
public class SystemItemImport {
    private String id;
    private String url;
    private String parentId;
    private SystemItemType type;
    private Long size;
}
