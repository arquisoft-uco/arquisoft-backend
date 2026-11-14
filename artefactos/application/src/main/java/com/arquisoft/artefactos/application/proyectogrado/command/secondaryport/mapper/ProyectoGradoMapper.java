package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;

public final class ProyectoGradoMapper {

    private ProyectoGradoMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoDomain proyecto) {
        return new ProyectoGradoEntity(
                proyecto.getId(), proyecto.getNombre(), proyecto.getEntregadoEn(),
                proyecto.getEstadoProyecto().getId(), proyecto.getOcurridoEn());
    }

    public static ProyectoGradoDomain toDomain(ProyectoGradoEntity entity) {
        return ProyectoGradoDomain.reconstruir(
                entity.id(), entity.nombre(), entity.entregadoEn(),
                EstadoProyecto.desde(entity.estadoProyecto()), entity.ocurridoEn());
    }
}
