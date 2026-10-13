package com.arquisoft.evaluaciones.application.estadoevaluacion.query.secondaryport;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;

import java.util.List;

public interface EstadoEvaluacionQueryOutputPort {

    List<EstadoEvaluacionReadModel> consultarTodos();
}
