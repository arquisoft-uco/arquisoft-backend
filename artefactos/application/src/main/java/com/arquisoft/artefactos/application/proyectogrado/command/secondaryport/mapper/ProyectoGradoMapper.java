package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;

public final class ProyectoGradoMapper {

    private ProyectoGradoMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoDomain proyecto) {
        return new ProyectoGradoEntity(proyecto.getId(), proyecto.getNombre());
    }
}
