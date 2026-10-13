package com.arquisoft.usuarios.application.usuario.query.secondaryport;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface UsuarioQueryOutputPort {

    PaginatedResult<UsuarioReadModel> consultarTodos(UsuarioCriteria criteria);
}
