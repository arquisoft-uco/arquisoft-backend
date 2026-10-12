package com.arquisoft.usuarios.application.bibliotecario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

public final class RemoverBibliotecarioMapper {

    private RemoverBibliotecarioMapper() {}

    public static BibliotecarioDomain toDomain(RemoverBibliotecarioCommand command) {
        return BibliotecarioDomain.crear(command.usuario());
    }
}
