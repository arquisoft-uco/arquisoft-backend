package com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;

public final class EstudianteArtefactoMapper {

    private EstudianteArtefactoMapper() {}

    public static EstudianteArtefactoEntity toEntity(EstudianteArtefactoDomain relacion) {
        return new EstudianteArtefactoEntity(relacion.getId(), relacion.getArtefacto(), relacion.getEstudiante());
    }

    public static ParEstudianteArtefacto toPar(EstudianteArtefactoEntity entity) {
        return new ParEstudianteArtefacto(entity.artefacto(), entity.estudiante());
    }
}
