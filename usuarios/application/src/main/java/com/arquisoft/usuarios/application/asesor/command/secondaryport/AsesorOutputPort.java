package com.arquisoft.usuarios.application.asesor.command.secondaryport;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;

import java.util.UUID;

public interface AsesorOutputPort {

    void guardar(AsesorEntity asesor);

    boolean existePorUsuario(UUID usuario);
}
