package com.example.yetanotherdisk.service;

import javax.validation.constraints.NotNull;

public interface SystemItemService {

    void deleteItem(@NotNull String id);
}
