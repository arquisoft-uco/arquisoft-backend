package com.arquisoft.usuarios.application.estudiante.command.secondaryport;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.util.UUID;

public interface EstudianteOutputPort {

    void guardar(EstudianteEntity estudiante);

    boolean existePorUsuario(UUID usuario);
}
