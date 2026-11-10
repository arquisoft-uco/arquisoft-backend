package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

public final class PaqueteEntregableMapper {

    private PaqueteEntregableMapper() {}

    public static PaqueteEntregableEntity toEntity(PaqueteEntregableDomain paquete) {
        return new PaqueteEntregableEntity(
                paquete.getId(),
                paquete.getEntregableProyectoGrado(),
                paquete.getBucket(),
                paquete.getClaveObjeto(),
                paquete.getNombreArchivo(),
                paquete.getTamanoBytes(),
                paquete.getGeneradoEn());
    }
}
