package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;

public final class ProyectoEstudianteAccesoMapper {

    private ProyectoEstudianteAccesoMapper() {}

    public static ProyectoEstudianteAccesoEntity toEntity(ProyectoEstudianteAccesoDomain domain) {
        return new ProyectoEstudianteAccesoEntity(
                domain.getProyecto(),
                domain.getEstudiante(),
                domain.isActivo(),
                domain.getOcurridoEn());
    }

    public static ProyectoEstudianteAccesoDomain toDomain(ProyectoEstudianteAccesoEntity entity) {
        return ProyectoEstudianteAccesoDomain.reconstruir(
                entity.proyecto(),
                entity.estudiante(),
                entity.activo(),
                entity.ocurridoEn());
    }
}
