package com.example.yetanotherdisk.mapper;

import com.example.yetanotherdisk.dto.SystemItemHistoryUnitResponse;
import com.example.yetanotherdisk.entity.SystemItemHistoryUnit;
import org.modelmapper.ModelMapper;

import javax.annotation.PostConstruct;

public class SystemItemHistoryMapper extends AbstractMapper<SystemItemHistoryUnit, SystemItemHistoryUnitResponse> {

    public SystemItemHistoryMapper(ModelMapper mapper) {
        super(mapper, SystemItemHistoryUnit.class, SystemItemHistoryUnitResponse.class);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(SystemItemHistoryUnit.class, SystemItemHistoryUnitResponse.class)
                .addMappings(m -> m.skip(SystemItemHistoryUnitResponse::setId))
                .setPostConverter(toDtoConverter());
    }

    @Override
    void mapSpecificFields(SystemItemHistoryUnit source, SystemItemHistoryUnitResponse destination) {
        destination.setId(source.getItemId());
    }
}
