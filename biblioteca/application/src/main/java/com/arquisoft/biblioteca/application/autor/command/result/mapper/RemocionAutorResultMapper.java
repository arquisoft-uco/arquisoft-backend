package com.arquisoft.biblioteca.application.autor.command.result.mapper;

import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;

import java.time.Instant;

public final class RemocionAutorResultMapper {

    private RemocionAutorResultMapper() {}

    public static RemocionAutorResult.Removida toResultRemovida(AutorDomain autor) {
        return new RemocionAutorResult.Removida(autor.getId());
    }

    public static RemocionAutorResult.Lapida toResultLapida(AutorDomain autor) {
        return new RemocionAutorResult.Lapida(autor.getId());
    }

    public static RemocionAutorResult.Descartada toResultDescartada(
            AutorDomain autor, Instant ocurridoEnVigente) {
        return new RemocionAutorResult.Descartada(autor.getId(), ocurridoEnVigente);
    }
}
