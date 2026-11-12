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

    public static PaqueteEntregableEntity toEntity(PaqueteEntregableJpaEntity jpa) {
        return new PaqueteEntregableEntity(
                jpa.getId(),
                jpa.getEntregableProyectoGradoId(),
                jpa.getBucket(),
                jpa.getClaveObjeto(),
                jpa.getNombreArchivo(),
                jpa.getTamanoBytes(),
                jpa.getGeneradoEn());
    }
}
