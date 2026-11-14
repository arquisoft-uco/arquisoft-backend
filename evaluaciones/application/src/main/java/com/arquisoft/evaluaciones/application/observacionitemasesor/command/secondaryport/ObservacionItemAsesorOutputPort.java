package com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;

import java.util.UUID;

public interface ObservacionItemAsesorOutputPort {

    void registrar(ObservacionItemAsesorEntity entity);

    boolean existePorEvaluacionYDescripcion(UUID evaluacionCuantitativaAsesor, String descripcion);

    boolean existeOtraConDescripcion(UUID observacion, String descripcion);

    void actualizarDescripcion(UUID id, String descripcion);
}
