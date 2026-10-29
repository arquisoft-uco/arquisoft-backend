package com.arquisoft.entregables.application.asesor.command.secondaryport;

import com.arquisoft.entregables.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorOutputPort {

    void guardar(AsesorEntity asesor);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(AsesorEntity asesor);

    Optional<AsesorEntity> obtenerPorId(UUID id);

    boolean existeVigentePorId(UUID id);
}
