package com.arquisoft.fichas.domain.estadofichaperfil.port.out;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilDomain;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilOutputPort {

    void guardar(EstadoFichaPerfilDomain aggregate);

    Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId);
}
