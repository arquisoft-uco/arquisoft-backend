package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.entity.EntregableProyectoAccesoJpaEntity;

public final class EntregableProyectoAccesoJpaMapper {

    private EntregableProyectoAccesoJpaMapper() {}

    public static EntregableProyectoAccesoJpaEntity toJpaEntity(EntregableProyectoAccesoEntity entity) {
        return EntregableProyectoAccesoJpaEntity.builder()
                .entregable(entity.entregable())
                .proyecto(entity.proyecto())
                .versionEntregable(entity.versionEntregable())
                .activo(entity.activo())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }

    public static EntregableProyectoAccesoEntity toEntity(EntregableProyectoAccesoJpaEntity jpaEntity) {
        return new EntregableProyectoAccesoEntity(
                jpaEntity.getEntregable(),
                jpaEntity.getProyecto(),
                jpaEntity.getVersionEntregable(),
                jpaEntity.isActivo(),
                jpaEntity.getOcurridoEn());
    }
}
