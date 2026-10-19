package com.arquisoft.artefactos.application.estadorevisionasesor.query.secondaryport;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;

import java.util.List;

public interface EstadoRevisionAsesorQueryOutputPort {

    List<EstadoRevisionAsesorReadModel> consultarTodos();
}
