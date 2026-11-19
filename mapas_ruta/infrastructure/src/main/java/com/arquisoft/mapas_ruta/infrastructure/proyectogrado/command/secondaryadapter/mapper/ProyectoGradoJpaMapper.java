package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEstadoProyectoGradoId(),
                jpaEntity.getCoordinadorId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getTituloProyecto(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getOcurridoEn(), UtilFecha.VACIO));
    }

    public static ProyectoGradoJpaEntity toJpaEntity(ProyectoGradoEntity entity) {
        return ProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .estadoProyectoGradoId(entity.estadoProyectoGrado())
                .coordinadorId(entity.coordinador())
                .fichaPerfilId(entity.fichaPerfil())
                .tituloProyecto(entity.tituloProyecto())
                .ocurridoEn(UtilFecha.VACIO.equals(entity.ocurridoEn()) ? null : entity.ocurridoEn())
                .build();
    }
}
