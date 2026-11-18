package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;

import java.util.List;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoQueryOutputPort {

    List<EstadoEntregableProyectoGradoReadModel> consultarPorEntregable(UUID entregableProyectoGrado);
}
