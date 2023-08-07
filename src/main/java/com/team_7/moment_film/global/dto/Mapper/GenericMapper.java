package com.team_7.moment_film.global.dto.Mapper;

import java.util.List;

public interface GenericMapper<D,E>{
    D toDto(E e);

    E toEntity(D d);

    List<D> toDtoList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);

}
