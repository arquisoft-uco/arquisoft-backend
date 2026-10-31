package com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;

public final class ObservacionMapper {

    private ObservacionMapper() {}

    public static ObservacionEntity toEntity(ObservacionDomain observacion) {
        return new ObservacionEntity(
                observacion.getId(),
                observacion.getRevisionAsesor(),
                observacion.getDescripcion(),
                observacion.getEstado().getId());
    }

    public static ObservacionDomain toDomain(ObservacionEntity entity) {
        return ObservacionDomain.reconstruir(
                entity.id(),
                entity.revisionAsesor(),
                entity.descripcion(),
                EstadoObservacion.desde(entity.estado()));
    }
}
