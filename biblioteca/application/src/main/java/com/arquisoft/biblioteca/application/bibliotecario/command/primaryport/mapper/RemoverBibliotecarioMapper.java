package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;

public final class RemoverBibliotecarioMapper {

    private RemoverBibliotecarioMapper() {}

    public static BibliotecarioDomain toDomain(RemoverBibliotecarioCommand command) {
        return BibliotecarioDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
