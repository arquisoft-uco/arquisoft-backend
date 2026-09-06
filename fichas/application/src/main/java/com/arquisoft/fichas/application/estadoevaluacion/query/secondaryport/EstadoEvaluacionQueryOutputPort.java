package com.arquisoft.fichas.application.estadoevaluacion.query.secondaryport;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;

import java.util.List;

public interface EstadoEvaluacionQueryOutputPort {

    List<EstadoEvaluacionReadModel> consultarTodos();
}
