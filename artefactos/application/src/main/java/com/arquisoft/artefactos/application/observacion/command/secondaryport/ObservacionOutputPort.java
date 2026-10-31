package com.arquisoft.artefactos.application.observacion.command.secondaryport;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;

import java.util.UUID;

public interface ObservacionOutputPort {

    void registrar(ObservacionEntity observacion);

    boolean existePorRevisionAsesorYDescripcion(UUID revisionAsesor, String descripcion);

    void eliminar(UUID observacion);
}
