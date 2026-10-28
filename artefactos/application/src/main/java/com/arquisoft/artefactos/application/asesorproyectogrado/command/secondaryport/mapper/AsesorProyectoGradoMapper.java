package com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

public final class AsesorProyectoGradoMapper {

    private AsesorProyectoGradoMapper() {}

    public static AsesorProyectoGradoDomain toDomain(AsesorProyectoGradoEntity entity) {
        return AsesorProyectoGradoDomain.reconstruir(
                entity.id(), entity.asesor(), entity.proyectoGrado(), entity.ocurridoEn(), entity.finalizadoEn());
    }

    public static AsesorProyectoGradoEntity toEntity(AsesorProyectoGradoDomain vinculo) {
        return new AsesorProyectoGradoEntity(
                vinculo.getId(), vinculo.getAsesor(), vinculo.getProyectoGrado(), vinculo.getOcurridoEn(),
                vinculo.getFinalizadoEn());
    }
}
