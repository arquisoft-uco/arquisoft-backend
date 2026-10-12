package com.arquisoft.biblioteca.application.bibliotecario.command.result.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;

import java.time.Instant;

public final class AgregacionBibliotecarioResultMapper {

    private AgregacionBibliotecarioResultMapper() {}

    public static AgregacionBibliotecarioResult.Agregada toResultAgregada(BibliotecarioDomain bibliotecario) {
        return new AgregacionBibliotecarioResult.Agregada(bibliotecario.getId());
    }

    public static AgregacionBibliotecarioResult.Reactivada toResultReactivada(BibliotecarioDomain bibliotecario) {
        return new AgregacionBibliotecarioResult.Reactivada(bibliotecario.getId());
    }

    public static AgregacionBibliotecarioResult.Duplicada toResultDuplicada(BibliotecarioDomain bibliotecario) {
        return new AgregacionBibliotecarioResult.Duplicada(bibliotecario.getId());
    }

    public static AgregacionBibliotecarioResult.Descartada toResultDescartada(
            BibliotecarioDomain bibliotecario, Instant ocurridoEnVigente) {
        return new AgregacionBibliotecarioResult.Descartada(bibliotecario.getId(), ocurridoEnVigente);
    }
}
