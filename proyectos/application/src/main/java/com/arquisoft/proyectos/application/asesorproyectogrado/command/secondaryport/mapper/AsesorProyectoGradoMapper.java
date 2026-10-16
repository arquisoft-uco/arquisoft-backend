package com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;

public final class AsesorProyectoGradoMapper {

    private AsesorProyectoGradoMapper() {}

    public static AsesorProyectoGradoEntity toEntity(AsesorProyectoGradoDomain vinculo) {
        return new AsesorProyectoGradoEntity(vinculo.getId(), vinculo.getAsesor(), vinculo.getProyectoGrado(),
                vinculo.getEstadoAsesor().getId(), vinculo.getFechaAsignado());
    }

    public static AsesorProyectoGradoDomain toDomain(AsesorProyectoGradoEntity entity) {
        return AsesorProyectoGradoDomain.reconstruir(entity.id(), entity.asesor(), entity.proyectoGrado(),
                EstadoAsesor.desde(entity.estadoAsesor()), entity.fechaAsignado());
    }
}
