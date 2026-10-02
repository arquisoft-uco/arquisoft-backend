package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionEvaluacionOutputPort {

    void registrarObservacion(ObservacionEvaluacionEntity observacion);

    boolean existePorEvaluacionYObservacion(UUID evaluacionFichaPerfil, String observacion);

    Optional<PertenenciaObservacionEvaluacionEntity> obtenerPertenencia(UUID observacionEvaluacion,
                                                                        UUID representanteComite);

    boolean existeOtraConMismoTexto(UUID observacionEvaluacion, String observacion);

    void actualizarObservacion(UUID observacionEvaluacion, String observacion);
}
