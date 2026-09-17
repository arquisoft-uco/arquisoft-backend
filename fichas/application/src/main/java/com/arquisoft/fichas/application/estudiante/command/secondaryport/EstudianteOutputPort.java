package com.arquisoft.fichas.application.estudiante.command.secondaryport;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteOutputPort {

    boolean existePorId(UUID id);

    List<EstudianteEntity> buscarPorIds(List<UUID> ids);

    List<UUID> buscarIdsVigentes(List<UUID> ids);

    void guardar(EstudianteEntity estudiante);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(EstudianteEntity estudiante);

    Optional<EstudianteEntity> obtenerPorId(UUID id);
}
