package com.arquisoft.proyectos.application.estadoasesor.query.secondaryport;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;

import java.util.List;

public interface EstadoAsesorQueryOutputPort {

    List<EstadoAsesorReadModel> consultarTodos();
}
