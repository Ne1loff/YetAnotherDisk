package com.example.yetanotherdisk.validator;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.example.yetanotherdisk.dto.SystemItemImport;
import com.example.yetanotherdisk.exception.badRequest.ValidationError;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Component
public class ValidatorImpl implements Validator {

    @Override
    public ZonedDateTime validateDateThenGet(String s) {
        try {
            return ZonedDateTime.parse(s);
        } catch (DateTimeParseException e) {
            throw new ValidationError();
        }
    }

    @Override
    public void isValidImport(SystemItemImport systemItemImport) {
        if (systemItemImport.getId() == null ||
                systemItemImport.getType() == null
        ) throw new ValidationError();

        if (systemItemImport.getType() == SystemItemType.FOLDER && systemItemImport.getSize() != null) throw new ValidationError();
        if (systemItemImport.getType() == SystemItemType.FILE && systemItemImport.getSize() == null) throw new ValidationError();
    }
}
