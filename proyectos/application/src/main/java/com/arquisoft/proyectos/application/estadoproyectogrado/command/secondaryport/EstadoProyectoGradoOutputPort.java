package com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoProyectoGradoOutputPort {

    void registrar(EstadoProyectoGradoEntity estado);

    Optional<EstadoProyectoGradoEntity> obtenerEstadoActual(UUID proyectoGrado);
}
