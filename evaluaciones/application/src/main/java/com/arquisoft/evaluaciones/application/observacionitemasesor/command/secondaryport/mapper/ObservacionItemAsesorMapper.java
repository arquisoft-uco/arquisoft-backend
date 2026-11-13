package com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ObservacionItemAsesorDomain;

public final class ObservacionItemAsesorMapper {

    private ObservacionItemAsesorMapper() {}

    public static ObservacionItemAsesorEntity toEntity(ObservacionItemAsesorDomain domain) {
        return new ObservacionItemAsesorEntity(
                domain.getId(), domain.getEvaluacionCuantitativaAsesor(), domain.getDescripcion());
    }
}
