package com.example.yetanotherdisk.validator;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.example.yetanotherdisk.dto.SystemItemImport;
import com.example.yetanotherdisk.exception.badRequest.ValidationError;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

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
    public void isValidImport(SystemItemImport itemImport) {
        if (itemImport.getId() == null || itemImport.getType() == null)
            throw new ValidationError();

        if (Objects.equals(itemImport.getParentId(), itemImport.getId()))
            throw new ValidationError();


        if (itemImport.getType() == SystemItemType.FOLDER) {
            if (itemImport.getSize() != null || itemImport.getUrl() != null)
                throw new ValidationError();
        } else {
            if (itemImport.getSize() == null
                    || itemImport.getSize() <= 0
                    || itemImport.getUrl() == null
                    || itemImport.getUrl().length() > 255)
                throw new ValidationError();
        }

    }
}
