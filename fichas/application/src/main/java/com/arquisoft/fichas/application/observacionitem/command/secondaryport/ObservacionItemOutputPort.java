package com.arquisoft.fichas.application.observacionitem.command.secondaryport;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionItemOutputPort {

    void registrarObservacion(ObservacionItemEntity observacion);

    long contarPorRevisionYObservacion(UUID revisionItemId, String observacion);

    Optional<ContextoObservacionItemEntity> obtenerContexto(UUID observacionItem);

    long contarOtrasIgualesEnRevision(UUID observacionItem, String observacion);

    void actualizarObservacion(UUID observacionItem, String observacion);
}
