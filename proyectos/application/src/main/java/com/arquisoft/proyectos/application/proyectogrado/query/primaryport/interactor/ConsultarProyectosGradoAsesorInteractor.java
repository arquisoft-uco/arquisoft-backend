package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarProyectosGradoAsesorInteractor
        extends Interactor<ConsultarProyectosGradoAsesorQuery, PaginatedResult<ProyectoGradoReadModel>> {}
