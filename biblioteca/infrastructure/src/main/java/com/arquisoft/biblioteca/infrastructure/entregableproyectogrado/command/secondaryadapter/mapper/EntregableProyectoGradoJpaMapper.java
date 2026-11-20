package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

public final class EntregableProyectoGradoJpaMapper {

    private EntregableProyectoGradoJpaMapper() {}

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoJpaEntity jpaEntity) {
        return new EntregableProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getVersionEntregable(),
                jpaEntity.getEstadoEntregableNombre(),
                alVacio(jpaEntity.getBucket()),
                alVacio(jpaEntity.getClaveObjeto()),
                alVacio(jpaEntity.getNombreArchivo()));
    }

    public static EntregableProyectoGradoJpaEntity toJpaEntity(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .proyectoGradoId(entity.proyectoGrado())
                .versionEntregable(entity.versionEntregable())
                .estadoEntregableNombre(entity.estadoEntregableNombre())
                .bucket(entity.bucket())
                .claveObjeto(entity.claveObjeto())
                .nombreArchivo(entity.nombreArchivo())
                .build();
    }

    private static String alVacio(String valor) {
        return UtilObjeto.aplicarPorDefecto(valor, UtilTexto.VACIO);
    }
}
