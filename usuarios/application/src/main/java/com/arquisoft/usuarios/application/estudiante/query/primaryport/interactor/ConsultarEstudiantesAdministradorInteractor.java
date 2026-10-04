package com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstudiantesAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<EstudianteReadModel>> {}
