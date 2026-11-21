package com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;

public final class EntregableProyectoGradoMapper {

    private EntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoDomain toDomain(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoDomain.reconstruir(
                entity.id(), entity.proyectoGrado(), entity.versionEntregable(),
                EstadoEntregable.desde(entity.estadoEntregable()), entity.claveObjeto(), entity.nombreArchivo());
    }

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoDomain entregable) {
        return new EntregableProyectoGradoEntity(
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersionEntregable(),
                entregable.getEstadoEntregable().getId(), entregable.getClaveObjeto(), entregable.getNombreArchivo());
    }
}
