package com.arquisoft.usuarios.application.jurado.query.usecase;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarJuradosVigentesUseCase
        extends UseCase<JuradoVigenteCriteria, PaginatedResult<JuradoVigenteReadModel>> {}
