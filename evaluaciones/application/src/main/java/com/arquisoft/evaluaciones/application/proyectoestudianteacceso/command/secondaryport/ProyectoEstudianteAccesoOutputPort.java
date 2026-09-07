package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoEstudianteAccesoOutputPort {

    Optional<ProyectoEstudianteAccesoEntity> buscarPorProyectoYEstudiante(UUID proyecto, UUID estudiante);

    void guardar(ProyectoEstudianteAccesoEntity entity);
}
