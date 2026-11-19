package com.arquisoft.fichas.application.observacionitem.command.secondaryport;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ResumenObservacionesRevisionEntity;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionItemOutputPort {

    void registrarObservacion(ObservacionItemEntity observacion);

    long contarPorRevisionYObservacion(UUID revisionItemId, String observacion);

    Optional<ContextoObservacionItemEntity> obtenerContexto(UUID observacionItem);

    long contarOtrasIgualesEnRevision(UUID observacionItem, String observacion);

    long contarOtrasSinCerrarEnRevision(UUID observacionItem, String estadoCerrado);

    void actualizarObservacion(UUID observacionItem, String observacion);

    void actualizarEstado(UUID observacionItem, String estadoActual, String estadoNuevo);

    void removerObservacion(UUID observacionItem);

    ResumenObservacionesRevisionEntity resumirPorRevision(UUID revisionItem, String estadoPendiente);

    void actualizarEstadoPorRevision(UUID revisionItem, String estadoActual, String estadoNuevo);
}
