package com.arquisoft.usuarios.application.usuario.query.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;

public interface ConsultarIdentidadUsuarioUseCase
        extends UseCase<IdentidadUsuarioCriteria, IdentidadUsuarioReadModel> {
}
