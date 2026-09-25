package com.arquisoft.usuarios.application.coordinador.command.secondaryport;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CoordinadorOutputPort {

    void guardar(CoordinadorEntity coordinador);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    void reactivar(UUID usuario);

    Optional<CoordinadorEntity> obtenerPorUsuario(UUID usuario);
}
