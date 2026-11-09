package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;

public final class PaqueteEntregableJpaMapper {

    private PaqueteEntregableJpaMapper() {}

    public static PaqueteEntregableJpaEntity toJpaEntity(PaqueteEntregableEntity entity) {
        return PaqueteEntregableJpaEntity.builder()
                .id(entity.id())
                .entregableProyectoGrado(entity.entregableProyectoGrado())
                .proyectoGrado(entity.proyectoGrado())
                .versionEntregable(entity.versionEntregable())
                .claveObjeto(entity.claveObjeto())
                .tamanoBytes(entity.tamanoBytes())
                .generadoEn(entity.generadoEn())
                .build();
    }
}
