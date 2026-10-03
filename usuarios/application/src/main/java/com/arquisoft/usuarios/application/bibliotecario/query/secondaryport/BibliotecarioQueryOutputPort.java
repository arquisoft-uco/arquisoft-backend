package com.arquisoft.usuarios.application.bibliotecario.query.secondaryport;

import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface BibliotecarioQueryOutputPort {

    PaginatedResult<BibliotecarioReadModel> consultarTodos(BibliotecarioCriteria criteria);
}
