package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoJpaEntity toJpaEntity(ProyectoGradoEntity entity) {
        return ProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .entregadoEn(UtilFecha.VACIO.equals(entity.entregadoEn()) ? null : entity.entregadoEn())
                .finalizadoEn(UtilFecha.VACIO.equals(entity.finalizadoEn()) ? null : entity.finalizadoEn())
                .build();
    }

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(
                jpaEntity.getId(), jpaEntity.getNombre(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEntregadoEn(), UtilFecha.VACIO),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getFinalizadoEn(), UtilFecha.VACIO));
    }
}
