package com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor;

import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresFichaVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AsesorFichaVigenteReadModel>> {}
