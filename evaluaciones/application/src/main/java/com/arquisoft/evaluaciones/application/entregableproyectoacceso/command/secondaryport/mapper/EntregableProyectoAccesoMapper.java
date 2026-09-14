package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;

public final class EntregableProyectoAccesoMapper {

    private EntregableProyectoAccesoMapper() {}

    public static EntregableProyectoAccesoEntity toEntity(EntregableProyectoAccesoDomain domain) {
        return new EntregableProyectoAccesoEntity(
                domain.getEntregable(),
                domain.getProyecto(),
                domain.getVersionEntregable(),
                domain.isActivo(),
                domain.getOcurridoEn());
    }

    public static EntregableProyectoAccesoDomain toDomain(EntregableProyectoAccesoEntity entity) {
        return EntregableProyectoAccesoDomain.reconstruir(
                entity.entregable(),
                entity.proyecto(),
                entity.versionEntregable(),
                entity.activo(),
                entity.ocurridoEn());
    }
}
