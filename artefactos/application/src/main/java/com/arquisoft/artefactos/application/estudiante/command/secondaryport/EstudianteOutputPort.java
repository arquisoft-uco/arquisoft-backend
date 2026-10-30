package com.arquisoft.artefactos.application.estudiante.command.secondaryport;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    List<EstudianteEntity> obtenerPorIds(List<UUID> ids);

    List<EstudianteEntity> obtenerVigentesVinculadosAlArtefacto(UUID artefacto);

    void guardar(EstudianteEntity estudiante);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(EstudianteEntity estudiante);

    Optional<EstudianteEntity> obtenerPorId(UUID id);

    void actualizar(EstudianteEntity estudiante);
}
