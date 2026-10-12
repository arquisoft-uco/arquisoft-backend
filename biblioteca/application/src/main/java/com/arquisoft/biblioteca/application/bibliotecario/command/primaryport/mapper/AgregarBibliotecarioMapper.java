package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;

public final class AgregarBibliotecarioMapper {

    private AgregarBibliotecarioMapper() {}

    public static BibliotecarioDomain toDomain(AgregarBibliotecarioCommand command) {
        return BibliotecarioDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
