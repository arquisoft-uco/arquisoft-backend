package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.mapper.AvanzarObservacionesRevisionMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.EnvioRevisionAsesorDomain;

public final class EnviarRevisionAsesorMapper {

    private EnviarRevisionAsesorMapper() {}

    public static EnvioRevisionAsesorDomain toDomain(EnviarRevisionAsesorCommand command) {
        var observaciones = AvanzarObservacionesRevisionMapper.toDomain(command.revisionAsesor());
        return EnvioRevisionAsesorDomain.crear(command.revisionAsesor(), command.asesor(), observaciones);
    }
}
