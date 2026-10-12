package com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

public final class BibliotecarioMapper {

    private BibliotecarioMapper() {}

    public static BibliotecarioDomain toDomain(BibliotecarioEntity entity) {
        return BibliotecarioDomain.reconstruir(entity.usuario(), entity.eliminadoEn());
    }

    public static BibliotecarioEntity toEntity(BibliotecarioDomain bibliotecario) {
        return new BibliotecarioEntity(bibliotecario.getUsuario(), bibliotecario.getEliminadoEn());
    }
}
