package com.arquisoft.entregables.application.paqueteentregable.query.secondaryport;

import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity.PaqueteEntregableAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface PaqueteEntregableAccesoQueryOutputPort {

    Optional<PaqueteEntregableAccesoEntity> obtenerPorEntregable(UUID entregableProyectoGrado);
}
