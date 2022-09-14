package com.example.yetanotherdisk.mapper;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.example.yetanotherdisk.dto.SystemItemImport;
import com.example.yetanotherdisk.dto.SystemItemResponse;
import com.example.yetanotherdisk.entity.SystemItem;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SystemItemMapper extends AbstractMapper<SystemItem, SystemItemResponse> {

    protected SystemItemMapper(ModelMapper mapper) {
        super(mapper, SystemItem.class, SystemItemResponse.class);
    }

    public SystemItem fromImport(SystemItemImport itemImport) {
        return mapper.map(itemImport, SystemItem.class);
    }

    private Converter<SystemItemImport, SystemItem> fromImportConverter() {
        return context -> {
            SystemItemImport source = context.getSource();
            SystemItem destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private void mapSpecificFields(SystemItemImport source, SystemItem destination) {
        destination.setSize(source.getSize() != null ? source.getSize() : 0);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(SystemItem.class, SystemItemResponse.class)
                .addMappings(m -> m.skip(SystemItemResponse::setChildren))
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(SystemItemImport.class, SystemItem.class)
                .addMappings(m -> m.skip(SystemItem::setSize))
                .setPostConverter(fromImportConverter());
    }

    @Override
    void mapSpecificFields(SystemItem source, SystemItemResponse destination) {
        if (destination.getType() == SystemItemType.FILE) {
            destination.setChildren(null);
        } else {
            destination.setChildren(
                    source.getChildren().stream()
                            .map(this::toDto)
                            .toList()
            );
        }
    }
}
