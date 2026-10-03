package com.arquisoft.usuarios.application.bibliotecario.query.usecase;

import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarBibliotecariosAdministradorUseCase
        extends UseCase<BibliotecarioCriteria, PaginatedResult<BibliotecarioReadModel>> {}
