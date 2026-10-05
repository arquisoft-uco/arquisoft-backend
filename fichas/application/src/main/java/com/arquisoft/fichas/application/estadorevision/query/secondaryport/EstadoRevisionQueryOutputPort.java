package com.arquisoft.fichas.application.estadorevision.query.secondaryport;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;

import java.util.List;

public interface EstadoRevisionQueryOutputPort {

    List<EstadoRevisionReadModel> consultarTodos();
}
