package com.arquisoft.biblioteca.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;

import java.time.Instant;
import java.util.UUID;

public final class AgregarProyectoGradoMapper {

    private AgregarProyectoGradoMapper() {}

    public static ProyectoGradoDomain toDomain(UUID id, String titulo, Instant ocurridoEn) {
        return ProyectoGradoDomain.crear(id, titulo, ocurridoEn);
    }
}
