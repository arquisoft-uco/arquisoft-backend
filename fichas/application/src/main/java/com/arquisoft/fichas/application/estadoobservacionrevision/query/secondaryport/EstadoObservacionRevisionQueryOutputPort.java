package com.arquisoft.fichas.application.estadoobservacionrevision.query.secondaryport;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;

import java.util.List;

public interface EstadoObservacionRevisionQueryOutputPort {

    List<EstadoObservacionRevisionReadModel> consultarTodos();
}
