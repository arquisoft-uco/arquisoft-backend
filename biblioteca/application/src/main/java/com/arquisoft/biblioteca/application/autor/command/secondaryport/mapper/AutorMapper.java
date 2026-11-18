package com.arquisoft.biblioteca.application.autor.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;

public final class AutorMapper {

    private AutorMapper() {}

    public static AutorDomain toDomain(AutorEntity entity) {
        return AutorDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static AutorEntity toEntity(AutorDomain autor) {
        return new AutorEntity(
                autor.getId(),
                autor.getIdentificador(),
                autor.getNombre(),
                autor.getEmail(),
                autor.getOcurridoEn(),
                autor.getEliminadoEn());
    }
}
