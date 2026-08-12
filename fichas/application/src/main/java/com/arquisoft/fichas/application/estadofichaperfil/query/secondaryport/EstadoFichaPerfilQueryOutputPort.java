package com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilQueryOutputPort {

    Optional<EstadoFichaPerfilEntity> obtenerEstadoActual(UUID fichaPerfilId);
}
