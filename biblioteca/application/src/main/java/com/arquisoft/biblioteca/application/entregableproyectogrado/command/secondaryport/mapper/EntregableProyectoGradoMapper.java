package com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public final class EntregableProyectoGradoMapper {

    private EntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoDomain toDomain(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoDomain.reconstruir(
                entity.id(), entity.proyectoGrado(), entity.versionEntregable(), entity.estadoEntregableNombre(),
                entity.bucket(), entity.claveObjeto(), entity.nombreArchivo());
    }

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoDomain entregable) {
        return new EntregableProyectoGradoEntity(
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersionEntregable(),
                entregable.getEstadoEntregableNombre(), entregable.getBucket(), entregable.getClaveObjeto(),
                entregable.getNombreArchivo());
    }
}
