package com.example.yetanotherdisk.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Error {

    @NotNull
    private int status;

    @NotNull
    private String message;
}
