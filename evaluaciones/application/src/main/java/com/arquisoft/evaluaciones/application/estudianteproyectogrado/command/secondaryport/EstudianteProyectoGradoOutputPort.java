package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoOutputPort {

    List<EstudianteProyectoGradoEntity> consultarPorProyectoGrado(UUID proyectoGrado);

    Optional<EstudianteProyectoGradoEntity> obtenerPorEstudianteYProyectoGrado(UUID estudiante, UUID proyectoGrado);

    void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos);

    void guardar(EstudianteProyectoGradoEntity vinculo);
}
