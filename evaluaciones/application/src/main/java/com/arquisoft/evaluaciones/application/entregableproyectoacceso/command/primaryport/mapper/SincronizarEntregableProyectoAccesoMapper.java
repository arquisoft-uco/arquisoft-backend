package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;

public final class SincronizarEntregableProyectoAccesoMapper {

    private SincronizarEntregableProyectoAccesoMapper() {}

    public static EntregableProyectoAccesoDomain toDomain(SincronizarEntregableProyectoAccesoCommand command) {
        return EntregableProyectoAccesoDomain.crear(
                command.entregable(), command.proyecto(), command.versionEntregable(), command.ocurridoEn());
    }
}
