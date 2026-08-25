package com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilOutputPort {

    void registrarEstadoInicial(EstadoFichaPerfilEntity estado);

    Optional<EstadoFichaPerfilEntity> obtenerEstadoActual(UUID fichaPerfilId);
}
