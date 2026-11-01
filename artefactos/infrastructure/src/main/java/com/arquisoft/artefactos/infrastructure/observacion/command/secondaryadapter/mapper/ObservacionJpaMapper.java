package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;

public final class ObservacionJpaMapper {

    private ObservacionJpaMapper() {}

    public static ObservacionJpaEntity toJpaEntity(ObservacionEntity entity) {
        return ObservacionJpaEntity.builder()
                .id(entity.id())
                .revisionAsesor(entity.revisionAsesor())
                .descripcion(entity.descripcion())
                .estado(entity.estado())
                .observacionOrigen(UtilUUID.esPorDefecto(entity.observacionOrigen()) ? null : entity.observacionOrigen())
                .build();
    }

    public static ObservacionEntity toEntity(ObservacionJpaEntity jpaEntity) {
        return new ObservacionEntity(
                jpaEntity.getId(),
                jpaEntity.getRevisionAsesor(),
                jpaEntity.getDescripcion(),
                jpaEntity.getEstado(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getObservacionOrigen(), UtilUUID.obtenerUUIDPorDefecto()));
    }
}
