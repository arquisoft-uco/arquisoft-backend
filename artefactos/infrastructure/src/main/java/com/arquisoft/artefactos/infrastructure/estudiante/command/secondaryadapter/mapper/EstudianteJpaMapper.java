package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }
}
