package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoOutputPort {

    Optional<EvaluacionCuantitativaJuradoEntity> obtenerPorId(UUID id);

    void cambiarPuntaje(UUID id, Integer nuevoPuntaje);
}
