package com.example.yetanotherdisk.mapper;

import com.example.yetanotherdisk.entity.SystemItem;
import com.example.yetanotherdisk.entity.SystemItemHistoryUnit;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class EntityMapper {

    private final ModelMapper mapper;


    public EntityMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public SystemItemHistoryUnit toHistoryEntity(SystemItem item) {
        return Objects.isNull(item) ?
                null : mapper.map(item, SystemItemHistoryUnit.class);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(SystemItem.class, SystemItemHistoryUnit.class)
                .addMappings(m -> m.skip(SystemItemHistoryUnit::setId))
                .setPostConverter(toHistoryEntityConverter());
    }

    private Converter<SystemItem, SystemItemHistoryUnit> toHistoryEntityConverter() {
        return context -> {
            SystemItem source = context.getSource();
            SystemItemHistoryUnit destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    void mapSpecificFields(SystemItem source, SystemItemHistoryUnit destination) {
        destination.setItemId(source.getId());
    }

}
