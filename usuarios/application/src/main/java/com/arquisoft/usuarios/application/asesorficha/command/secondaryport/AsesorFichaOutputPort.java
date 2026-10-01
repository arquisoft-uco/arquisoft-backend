package com.arquisoft.usuarios.application.asesorficha.command.secondaryport;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaOutputPort {

    void guardar(AsesorFichaEntity asesorFicha);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    void reactivar(UUID usuario);

    Optional<AsesorFichaEntity> obtenerPorUsuario(UUID usuario);
}
