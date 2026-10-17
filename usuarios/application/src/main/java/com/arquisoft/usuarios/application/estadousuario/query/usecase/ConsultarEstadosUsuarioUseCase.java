package com.arquisoft.usuarios.application.estadousuario.query.usecase;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.shared.usecase.SupplierUseCase;

import java.util.List;

public interface ConsultarEstadosUsuarioUseCase extends SupplierUseCase<List<EstadoUsuarioReadModel>> {
}
