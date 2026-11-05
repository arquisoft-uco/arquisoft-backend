package com.arquisoft.usuarios.application.usuario.query.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioPorCorreoCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;

public interface ConsultarUsuarioPorCorreoUseCase
        extends UseCase<UsuarioPorCorreoCriteria, UsuarioPorCorreoReadModel> {
}
