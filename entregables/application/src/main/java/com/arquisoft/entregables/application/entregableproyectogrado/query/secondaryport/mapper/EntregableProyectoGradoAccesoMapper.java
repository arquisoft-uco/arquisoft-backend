package com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.entity.EntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;

public final class EntregableProyectoGradoAccesoMapper {

    private EntregableProyectoGradoAccesoMapper() {}

    public static EntregableProyectoGradoDomain toDomain(EntregableProyectoGradoAccesoEntity entity) {
        return EntregableProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.proyectoGrado(),
                entity.version(),
                UtilUUID.obtenerUUIDPorDefecto(),
                entity.proyectoCancelado());
    }
}
