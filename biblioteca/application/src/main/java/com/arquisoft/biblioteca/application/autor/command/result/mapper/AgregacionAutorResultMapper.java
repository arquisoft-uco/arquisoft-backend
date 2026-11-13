package com.arquisoft.biblioteca.application.autor.command.result.mapper;

import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;

import java.time.Instant;

public final class AgregacionAutorResultMapper {

    private AgregacionAutorResultMapper() {}

    public static AgregacionAutorResult.Agregada toResultAgregada(AutorDomain autor) {
        return new AgregacionAutorResult.Agregada(autor.getId());
    }

    public static AgregacionAutorResult.Reactivada toResultReactivada(AutorDomain autor) {
        return new AgregacionAutorResult.Reactivada(autor.getId());
    }

    public static AgregacionAutorResult.Duplicada toResultDuplicada(AutorDomain autor) {
        return new AgregacionAutorResult.Duplicada(autor.getId());
    }

    public static AgregacionAutorResult.Descartada toResultDescartada(
            AutorDomain autor, Instant ocurridoEnVigente) {
        return new AgregacionAutorResult.Descartada(autor.getId(), ocurridoEnVigente);
    }
}
