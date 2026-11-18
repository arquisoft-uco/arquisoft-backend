package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity.PaqueteEntregableAccesoEntity;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository.PaqueteEntregableAccesoJpaQueryEntity;

public final class PaqueteEntregableAccesoQueryMapper {

    private PaqueteEntregableAccesoQueryMapper() {}

    public static PaqueteEntregableAccesoEntity toEntity(PaqueteEntregableAccesoJpaQueryEntity jpa) {
        return new PaqueteEntregableAccesoEntity(
                jpa.getId(),
                jpa.getEntregableProyectoGradoId(),
                jpa.getBucket(),
                jpa.getClaveObjeto(),
                jpa.getNombreArchivo());
    }
}
