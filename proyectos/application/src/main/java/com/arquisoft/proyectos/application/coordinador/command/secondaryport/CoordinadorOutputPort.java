package com.arquisoft.proyectos.application.coordinador.command.secondaryport;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CoordinadorOutputPort {

    void guardar(CoordinadorEntity coordinador);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(CoordinadorEntity coordinador);

    Optional<CoordinadorEntity> obtenerPorId(UUID id);

    void actualizar(CoordinadorEntity coordinador);
}
