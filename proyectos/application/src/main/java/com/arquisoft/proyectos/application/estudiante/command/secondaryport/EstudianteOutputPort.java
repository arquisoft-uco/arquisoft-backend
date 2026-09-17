package com.arquisoft.proyectos.application.estudiante.command.secondaryport;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    void guardar(EstudianteEntity estudiante);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(EstudianteEntity estudiante);

    Optional<EstudianteEntity> obtenerPorId(UUID id);
}
