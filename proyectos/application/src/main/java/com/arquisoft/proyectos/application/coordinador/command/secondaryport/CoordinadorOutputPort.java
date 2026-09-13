package com.arquisoft.proyectos.application.coordinador.command.secondaryport;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorOutputPort {

    void guardar(CoordinadorEntity coordinador);

    Optional<CoordinadorEntity> obtenerPorId(UUID id);
}
