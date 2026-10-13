package com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionItemJuradoOutputPort {

    void registrar(ObservacionItemJuradoEntity entity);

    boolean existePorEvaluacionYDescripcion(UUID evaluacionCuantitativaJurado, String descripcion);

    Optional<ObservacionItemJuradoEntity> obtenerPorId(UUID id);

    boolean existeOtraConDescripcion(UUID observacion, String descripcion);

    void actualizarDescripcion(UUID id, String descripcion);
}
