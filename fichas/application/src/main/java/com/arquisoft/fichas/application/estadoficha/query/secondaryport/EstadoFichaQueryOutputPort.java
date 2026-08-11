package com.arquisoft.fichas.application.estadoficha.query.secondaryport;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;

import java.util.List;

public interface EstadoFichaQueryOutputPort {

    List<EstadoFichaReadModel> findAll();
}
