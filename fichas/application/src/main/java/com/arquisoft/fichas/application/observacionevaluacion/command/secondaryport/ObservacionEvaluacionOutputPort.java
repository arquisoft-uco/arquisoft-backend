package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;

import java.util.UUID;

public interface ObservacionEvaluacionOutputPort {

    void registrarObservacion(ObservacionEvaluacionEntity observacion);

    boolean existePorEvaluacionYObservacion(UUID evaluacionFichaPerfil, String observacion);
}
