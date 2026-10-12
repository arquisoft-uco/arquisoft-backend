package com.arquisoft.usuarios.application.usuario.query.usecase;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarUsuariosAdministradorUseCase
        extends UseCase<UsuarioCriteria, PaginatedResult<UsuarioReadModel>> {}
