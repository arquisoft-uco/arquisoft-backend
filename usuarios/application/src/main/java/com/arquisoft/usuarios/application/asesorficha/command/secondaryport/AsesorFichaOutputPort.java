package com.arquisoft.usuarios.application.asesorficha.command.secondaryport;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;

import java.util.UUID;

public interface AsesorFichaOutputPort {

    void guardar(AsesorFichaEntity asesorFicha);

    boolean existePorUsuario(UUID usuario);
}
