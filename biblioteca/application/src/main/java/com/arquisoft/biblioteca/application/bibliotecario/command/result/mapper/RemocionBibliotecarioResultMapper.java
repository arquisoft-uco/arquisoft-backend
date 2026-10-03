package com.arquisoft.biblioteca.application.bibliotecario.command.result.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;

import java.time.Instant;

public final class RemocionBibliotecarioResultMapper {

    private RemocionBibliotecarioResultMapper() {}

    public static RemocionBibliotecarioResult.Removida toResultRemovida(BibliotecarioDomain bibliotecario) {
        return new RemocionBibliotecarioResult.Removida(bibliotecario.getId());
    }

    public static RemocionBibliotecarioResult.Lapida toResultLapida(BibliotecarioDomain bibliotecario) {
        return new RemocionBibliotecarioResult.Lapida(bibliotecario.getId());
    }

    public static RemocionBibliotecarioResult.Descartada toResultDescartada(
            BibliotecarioDomain bibliotecario, Instant ocurridoEnVigente) {
        return new RemocionBibliotecarioResult.Descartada(bibliotecario.getId(), ocurridoEnVigente);
    }
}
