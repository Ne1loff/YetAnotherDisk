package com.example.yetanotherdisk.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
public class SystemItemImportRequest {

    private List<SystemItemImport> items;

    private String updateDate;
}
