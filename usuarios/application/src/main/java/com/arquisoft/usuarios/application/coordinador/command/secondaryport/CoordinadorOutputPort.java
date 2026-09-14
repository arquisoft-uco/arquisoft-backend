package com.arquisoft.usuarios.application.coordinador.command.secondaryport;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;

import java.util.UUID;

public interface CoordinadorOutputPort {

    void guardar(CoordinadorEntity coordinador);

    boolean existePorUsuario(UUID usuario);
}
