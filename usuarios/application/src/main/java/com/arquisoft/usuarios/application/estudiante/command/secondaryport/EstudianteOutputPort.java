package com.arquisoft.usuarios.application.estudiante.command.secondaryport;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    void guardar(EstudianteEntity estudiante);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    void reactivar(UUID usuario);

    Optional<EstudianteEntity> obtenerPorUsuario(UUID usuario);
}
