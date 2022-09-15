package com.example.yetanotherdisk.mapper;

import com.example.yetanotherdisk.dto.AbstractDto;
import com.example.yetanotherdisk.entity.AbstractEntity;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.util.Objects;

public abstract class AbstractMapper<E extends AbstractEntity, D extends AbstractDto> implements Mapper<E, D> {

    protected final ModelMapper mapper;

    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    protected AbstractMapper(ModelMapper mapper, Class<E> entityClass, Class<D> dtoClass) {

        this.mapper = mapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public E toEntity(D dto) {
        return Objects.isNull(dto) ?
                null : mapper.map(dto, entityClass);
    }

    @Override
    public D toDto(E entity) {
        return Objects.isNull(entity) ?
                null : mapper.map(entity, dtoClass);
    }

    Converter<E, D> toDtoConverter() {
        return context -> {
            E source = context.getSource();
            D destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    Converter<D, E> toEntityConverter() {
        return context -> {
            D source = context.getSource();
            E destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    void mapSpecificFields(E source, D destination) {
    }

    void mapSpecificFields(D source, E destination) {
    }
}
