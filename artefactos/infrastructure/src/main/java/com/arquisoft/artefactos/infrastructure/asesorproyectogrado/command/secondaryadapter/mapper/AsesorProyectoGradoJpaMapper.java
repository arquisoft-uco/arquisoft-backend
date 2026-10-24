package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class AsesorProyectoGradoJpaMapper {

    private AsesorProyectoGradoJpaMapper() {}

    public static AsesorProyectoGradoEntity toEntity(AsesorProyectoGradoJpaEntity jpaEntity) {
        return new AsesorProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getAsesorId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getFinalizadoEn(), UtilFecha.VACIO));
    }

    public static AsesorProyectoGradoJpaEntity toJpaEntity(AsesorProyectoGradoEntity entity) {
        return AsesorProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .asesorId(entity.asesor())
                .proyectoGradoId(entity.proyectoGrado())
                .ocurridoEn(entity.ocurridoEn())
                .finalizadoEn(aColumna(entity.finalizadoEn()))
                .build();
    }

    private static Instant aColumna(Instant finalizadoEn) {
        return UtilFecha.VACIO.equals(finalizadoEn) ? null : finalizadoEn;
    }
}
