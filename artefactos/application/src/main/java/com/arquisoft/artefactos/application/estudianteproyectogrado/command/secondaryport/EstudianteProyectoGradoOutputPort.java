package com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoOutputPort {

    List<PertenenciaProyectoGradoEntity> obtenerPertenenciasVigentes(List<UUID> artefactos, List<UUID> estudiantes);

    List<EstudianteProyectoGradoEntity> obtenerPorProyectoGradoYEstudiantes(
            UUID proyectoGrado, List<UUID> estudiantes);

    Optional<EstudianteProyectoGradoEntity> obtenerPorEstudianteYProyectoGrado(UUID estudiante, UUID proyectoGrado);

    void guardar(EstudianteProyectoGradoEntity vinculo);

    void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos);
}
