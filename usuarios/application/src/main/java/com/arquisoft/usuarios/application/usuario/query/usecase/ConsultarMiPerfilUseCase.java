package com.arquisoft.usuarios.application.usuario.query.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;

public interface ConsultarMiPerfilUseCase extends UseCase<PerfilUsuarioCriteria, PerfilUsuarioReadModel> {
}
