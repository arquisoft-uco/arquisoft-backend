package com.arquisoft.usuarios.application.asesor.command.secondaryport;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorOutputPort {

    void guardar(AsesorEntity asesor);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    void reactivar(UUID usuario);

    Optional<AsesorEntity> obtenerPorUsuario(UUID usuario);
}
