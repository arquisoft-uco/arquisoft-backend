package com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor;

import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresFichaAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AsesorFichaReadModel>> {}
