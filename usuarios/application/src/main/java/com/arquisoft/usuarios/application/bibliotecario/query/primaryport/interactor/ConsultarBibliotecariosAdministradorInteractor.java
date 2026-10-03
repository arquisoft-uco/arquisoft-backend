package com.arquisoft.usuarios.application.bibliotecario.query.primaryport.interactor;

import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarBibliotecariosAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<BibliotecarioReadModel>> {}
