package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstadosEntregableProyectoGradoInteractor
        extends Interactor<ConsultarEstadosEntregableProyectoGradoQuery, List<EstadoEntregableProyectoGradoReadModel>> {}
