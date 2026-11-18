package com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoJpaEntity toJpaEntity(ProyectoGradoEntity entity) {
        return ProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .estadoProyectoId(entity.estadoProyecto())
                .ocurridoEn(UtilFecha.VACIO.equals(entity.ocurridoEn()) ? null : entity.ocurridoEn())
                .build();
    }

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(
                jpaEntity.getId(), jpaEntity.getNombre(), jpaEntity.getEstadoProyectoId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getOcurridoEn(), UtilFecha.VACIO));
    }
}
