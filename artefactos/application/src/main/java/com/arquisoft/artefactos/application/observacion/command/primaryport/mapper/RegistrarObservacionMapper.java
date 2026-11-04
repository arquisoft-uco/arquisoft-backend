package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;

public final class RegistrarObservacionMapper {

    private RegistrarObservacionMapper() {}

    public static RegistroObservacionDomain toDomain(RegistrarObservacionCommand command) {
        var observacion = ObservacionDomain.crear(command.revisionAsesor(), command.descripcion());
        return RegistroObservacionDomain.crear(observacion, command.asesor());
    }
}
