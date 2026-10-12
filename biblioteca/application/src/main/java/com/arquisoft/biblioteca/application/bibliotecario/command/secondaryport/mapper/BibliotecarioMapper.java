package com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;

public final class BibliotecarioMapper {

    private BibliotecarioMapper() {}

    public static BibliotecarioDomain toDomain(BibliotecarioEntity entity) {
        return BibliotecarioDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static BibliotecarioEntity toEntity(BibliotecarioDomain bibliotecario) {
        return new BibliotecarioEntity(
                bibliotecario.getId(),
                bibliotecario.getIdentificador(),
                bibliotecario.getNombre(),
                bibliotecario.getEmail(),
                bibliotecario.getOcurridoEn(),
                bibliotecario.getEliminadoEn());
    }
}
