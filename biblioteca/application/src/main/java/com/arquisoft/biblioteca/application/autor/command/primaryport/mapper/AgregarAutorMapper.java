package com.arquisoft.biblioteca.application.autor.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.autor.command.primaryport.model.AgregarAutorCommand;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;

public final class AgregarAutorMapper {

    private AgregarAutorMapper() {}

    public static AutorDomain toDomain(AgregarAutorCommand command) {
        return AutorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
