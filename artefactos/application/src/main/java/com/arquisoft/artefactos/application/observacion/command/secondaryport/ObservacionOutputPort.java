package com.arquisoft.artefactos.application.observacion.command.secondaryport;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionOutputPort {

    void registrar(ObservacionEntity observacion);

    boolean existePorRevisionAsesorYDescripcion(UUID revisionAsesor, String descripcion);

    boolean existenPorRevisionAsesor(UUID revisionAsesor);

    boolean existenNoCerradasDeVersionAnterior(UUID revisionAsesor, String estadoCerrada);

    void eliminar(UUID observacion);

    int avanzarEstadoPorRevisionAsesor(UUID revisionAsesor, String estadoOrigen, String estadoDestino);

    Optional<ObservacionEntity> obtenerPorId(UUID id);

    void actualizarEstado(UUID observacion, String estado);

    void devolverEstado(UUID observacion, String estadoActual, String estadoDestino);
}
