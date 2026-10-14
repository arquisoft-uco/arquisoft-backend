package com.arquisoft.evaluaciones.application.jurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface JuradoOutputPort {

    Set<UUID> consultarVigentesPorIds(Set<UUID> jurados);

    void guardar(JuradoEntity jurado);

    void reactivar(JuradoEntity jurado);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    Optional<JuradoEntity> obtenerPorId(UUID id);
}
