package com.arquisoft.entregables.application.estadoentregable.query.secondaryport;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;

import java.util.List;

public interface EstadoEntregableQueryOutputPort {

    List<EstadoEntregableReadModel> findAll();
}
