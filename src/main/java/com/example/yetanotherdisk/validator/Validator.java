package com.example.yetanotherdisk.validator;

import com.example.yetanotherdisk.dto.SystemItemImport;

import java.time.ZonedDateTime;

public interface Validator {

    public ZonedDateTime validateDateThenGet(String s);

    void isValidImport(SystemItemImport systemItemImport);
}
