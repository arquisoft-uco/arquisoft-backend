package com.arquisoft.biblioteca.application.autor.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.autor.command.primaryport.model.RemoverAutorCommand;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;

public final class RemoverAutorMapper {

    private RemoverAutorMapper() {}

    public static AutorDomain toDomain(RemoverAutorCommand command) {
        return AutorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
