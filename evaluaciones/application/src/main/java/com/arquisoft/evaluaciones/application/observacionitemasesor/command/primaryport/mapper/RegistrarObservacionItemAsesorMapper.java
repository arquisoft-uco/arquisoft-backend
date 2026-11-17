package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;

public final class RegistrarObservacionItemAsesorMapper {

    private RegistrarObservacionItemAsesorMapper() {}

    public static RegistroObservacionItemAsesorDomain toDomain(RegistrarObservacionItemAsesorCommand command) {
        var observacion = ObservacionItemAsesorDomain.crear(
                command.evaluacionCuantitativaAsesor(), command.descripcion());
        return RegistroObservacionItemAsesorDomain.crear(observacion, command.asesor());
    }
}
