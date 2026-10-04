package com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoOutputPort {

    void vincular(List<EstudianteProyectoGradoEntity> vinculos);

    long contarPorProyectoGrado(UUID proyectoGrado);
}
