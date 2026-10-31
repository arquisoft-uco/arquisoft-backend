package com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoOutputPort {

    boolean existePorArtefactoYEstudiante(UUID artefacto, UUID estudiante);

    void vincularTodos(List<EstudianteArtefactoEntity> vinculos);

    List<EstudianteArtefactoEntity> obtenerVinculados(UUID artefacto, List<UUID> estudiantes);

    void eliminarPorArtefactoYEstudiante(UUID artefacto, UUID estudiante);

    void eliminarPorProyectoGradoYEstudiante(UUID proyectoGrado, UUID estudiante);

    void eliminarExceptoConservados(UUID artefacto, List<UUID> conservar);

    List<UUID> obtenerVigentesVinculados(UUID artefacto);
}
