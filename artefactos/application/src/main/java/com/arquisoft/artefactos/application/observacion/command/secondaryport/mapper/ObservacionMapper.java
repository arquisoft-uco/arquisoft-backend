package com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;

public final class ObservacionMapper {

    private ObservacionMapper() {}

    public static ObservacionEntity toEntity(ObservacionDomain observacion) {
        return new ObservacionEntity(
                observacion.getId(),
                observacion.getRevisionAsesor(),
                observacion.getDescripcion(),
                observacion.getEstado().getId());
    }
}
