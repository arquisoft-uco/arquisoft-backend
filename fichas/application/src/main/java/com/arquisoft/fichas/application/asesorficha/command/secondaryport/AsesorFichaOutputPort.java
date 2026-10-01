package com.arquisoft.fichas.application.asesorficha.command.secondaryport;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaOutputPort {

    Optional<AsesorFichaEntity> obtenerVigentePorId(UUID id);

    void guardar(AsesorFichaEntity asesorFicha);

    Optional<AsesorFichaEntity> obtenerPorId(UUID id);

    void actualizar(AsesorFichaEntity asesorFicha);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(AsesorFichaEntity asesorFicha);
}
