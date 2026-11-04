package com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;

public final class EstadoProyectoGradoMapper {

    private EstadoProyectoGradoMapper() {}

    public static EstadoProyectoGradoEntity toEntity(EstadoProyectoGradoDomain estado) {
        return new EstadoProyectoGradoEntity(
                estado.getId(),
                estado.getProyectoGrado(),
                estado.getEstadoProyecto().getId(),
                estado.getFechaActualizacion());
    }

    public static EstadoProyectoGradoDomain toDomain(EstadoProyectoGradoEntity entity) {
        return EstadoProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.proyectoGrado(),
                EstadoProyecto.desde(entity.estadoProyecto()),
                entity.fechaActualizacion());
    }
}
