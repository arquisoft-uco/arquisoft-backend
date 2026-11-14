package com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity.PaqueteEntregableAccesoEntity;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;

public final class PaqueteEntregableAccesoMapper {

    private PaqueteEntregableAccesoMapper() {}

    public static PaqueteEntregableDomain toDomain(PaqueteEntregableAccesoEntity entity) {
        return PaqueteEntregableDomain.reconstruir(
                entity.id(),
                entity.entregableProyectoGrado(),
                entity.bucket(),
                entity.claveObjeto(),
                entity.nombreArchivo(),
                UtilNumero.CERO,
                UtilFecha.VACIO);
    }
}
