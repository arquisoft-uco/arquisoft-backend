package com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilQueryOutputPort {

    Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId);
}
