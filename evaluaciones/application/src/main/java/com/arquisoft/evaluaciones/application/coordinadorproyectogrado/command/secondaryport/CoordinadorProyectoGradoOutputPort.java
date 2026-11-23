package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorProyectoGradoOutputPort {

    void guardar(CoordinadorProyectoGradoEntity coordinador);

    boolean existePorProyectoGrado(UUID proyectoGrado);

    Optional<CoordinadorProyectoGradoEntity> obtenerPorEvaluacion(UUID evaluacion);

    Optional<CoordinadorProyectoGradoEntity> obtenerPorProyectoGrado(UUID proyectoGrado);
}
