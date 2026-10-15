package com.arquisoft.usuarios.application.jurado.query.primaryport.interactor;

import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarJuradosVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<JuradoVigenteReadModel>> {}
