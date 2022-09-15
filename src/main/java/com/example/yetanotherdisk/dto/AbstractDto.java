package com.example.yetanotherdisk.dto;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public abstract class AbstractDto implements Serializable {
    private String id;
    private SystemItemType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Z")
    private ZonedDateTime date;
}
