package com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public final class ProyectoGradoMapper {

    private ProyectoGradoMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoDomain proyecto) {
        return new ProyectoGradoEntity(
                proyecto.getId(),
                proyecto.getFichaPerfil(),
                proyecto.getTituloProyecto(),
                proyecto.getCoordinador(),
                proyecto.getEstadoProyectoGrado().getId());
    }

    public static ProyectoGradoDomain toDomain(ProyectoGradoEntity entity) {
        return ProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.fichaPerfil(),
                entity.tituloProyecto(),
                entity.coordinador(),
                EstadoProyectoGrado.desde(entity.estadoProyectoGrado()));
    }
}
