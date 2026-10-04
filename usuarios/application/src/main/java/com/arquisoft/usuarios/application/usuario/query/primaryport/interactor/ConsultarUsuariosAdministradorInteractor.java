package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor;

import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarUsuariosAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<UsuarioReadModel>> {}
