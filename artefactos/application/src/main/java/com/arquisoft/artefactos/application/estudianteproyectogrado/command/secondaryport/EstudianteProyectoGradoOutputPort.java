package com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoOutputPort {

    List<UUID> obtenerEstudiantesVigentesDelArtefacto(UUID artefacto, List<UUID> estudiantes);

    List<EstudianteProyectoGradoEntity> obtenerPorProyectoGradoYEstudiantes(
            UUID proyectoGrado, List<UUID> estudiantes);

    Optional<EstudianteProyectoGradoEntity> obtenerPorEstudianteYProyectoGrado(UUID estudiante, UUID proyectoGrado);

    void guardar(EstudianteProyectoGradoEntity vinculo);

    void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos);
}
