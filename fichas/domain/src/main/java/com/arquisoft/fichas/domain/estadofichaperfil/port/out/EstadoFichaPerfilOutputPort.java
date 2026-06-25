package com.arquisoft.fichas.domain.estadofichaperfil.port.out;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;

public interface EstadoFichaPerfilOutputPort {

    void guardar(EstadoFichaPerfilAggregate aggregate);
}
