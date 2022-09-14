package com.example.yetanotherdisk.mapper;

import com.example.yetanotherdisk.dto.AbstractDto;
import com.example.yetanotherdisk.entity.AbstractEntity;

public interface Mapper<E extends AbstractEntity, D extends AbstractDto> {

    E toEntity(D dto);

    D toDto(E entity);
}
