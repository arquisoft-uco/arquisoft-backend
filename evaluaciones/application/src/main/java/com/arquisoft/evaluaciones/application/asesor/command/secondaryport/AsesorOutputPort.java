package com.arquisoft.evaluaciones.application.asesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorOutputPort {

    void guardar(AsesorEntity asesor);

    void reactivar(AsesorEntity asesor);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    Optional<AsesorEntity> obtenerPorId(UUID id);
}
