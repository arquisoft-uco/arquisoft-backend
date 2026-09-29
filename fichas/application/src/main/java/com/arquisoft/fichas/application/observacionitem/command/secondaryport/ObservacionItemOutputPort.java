package com.arquisoft.fichas.application.observacionitem.command.secondaryport;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;

import java.util.UUID;

public interface ObservacionItemOutputPort {

    void registrarObservacion(ObservacionItemEntity observacion);

    long contarPorRevisionYObservacion(UUID revisionItemId, String observacion);
}
