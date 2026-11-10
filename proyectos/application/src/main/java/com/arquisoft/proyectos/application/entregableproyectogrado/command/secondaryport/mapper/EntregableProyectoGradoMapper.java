package com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;

public final class EntregableProyectoGradoMapper {

    private EntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoDomain entregable) {
        return new EntregableProyectoGradoEntity(
                entregable.getId(),
                entregable.getProyectoGrado(),
                entregable.getVersion(),
                entregable.getEstadoEntregable().getId(),
                entregable.getOcurridoEn());
    }

    public static EntregableProyectoGradoDomain toDomain(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.proyectoGrado(),
                entity.version(),
                EstadoEntregable.desde(entity.estadoEntregable()),
                entity.ocurridoEn());
    }
}
