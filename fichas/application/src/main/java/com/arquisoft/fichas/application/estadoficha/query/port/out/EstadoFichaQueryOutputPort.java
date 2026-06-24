package com.arquisoft.fichas.application.estadoficha.query.port.out;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;

import java.util.Optional;

public interface EstadoFichaQueryOutputPort {

    Optional<EstadoFichaReadModel> buscarPorNombre(String nombre);
}
