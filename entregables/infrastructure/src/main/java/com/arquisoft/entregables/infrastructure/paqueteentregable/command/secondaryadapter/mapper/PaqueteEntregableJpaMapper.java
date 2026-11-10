package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;

public final class PaqueteEntregableJpaMapper {

    private PaqueteEntregableJpaMapper() {}

    public static PaqueteEntregableJpaEntity toJpaEntity(PaqueteEntregableEntity entity) {
        return PaqueteEntregableJpaEntity.builder()
                .id(entity.id())
                .entregableProyectoGradoId(entity.entregableProyectoGrado())
                .bucket(entity.bucket())
                .claveObjeto(entity.claveObjeto())
                .nombreArchivo(entity.nombreArchivo())
                .tamanoBytes(entity.tamanoBytes())
                .generadoEn(entity.generadoEn())
                .build();
    }
}
