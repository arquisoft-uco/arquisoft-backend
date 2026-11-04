package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.shared.interactor.SupplierInteractor;

import java.util.List;

public interface ConsultarEstadosPosiblesAsesorInteractor
        extends SupplierInteractor<List<EstadoAsesorReadModel>> {}
