package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.criteria.EstadoEntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstadosEntregableProyectoGradoUseCase
        extends UseCase<EstadoEntregableProyectoGradoCriteria, List<EstadoEntregableProyectoGradoReadModel>> {}
