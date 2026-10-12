package com.arquisoft.usuarios.application.jurado.command.secondaryport;

import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface JuradoOutputPort {

    void guardar(JuradoEntity jurado);

    void reactivar(UUID usuario);

    Optional<JuradoEntity> obtenerPorUsuario(UUID usuario);
}
