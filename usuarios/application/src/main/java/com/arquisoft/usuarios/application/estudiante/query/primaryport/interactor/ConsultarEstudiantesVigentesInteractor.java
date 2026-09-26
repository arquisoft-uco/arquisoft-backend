package com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstudiantesVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<EstudianteVigenteReadModel>> {}
