package com.arquisoft.fichas.domain.estadofichaperfil.port.out;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilOutputPort {

    void guardar(EstadoFichaPerfilAggregate aggregate);

    Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId);
}
