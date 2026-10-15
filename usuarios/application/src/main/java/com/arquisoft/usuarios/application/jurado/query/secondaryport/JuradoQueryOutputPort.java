package com.arquisoft.usuarios.application.jurado.query.secondaryport;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface JuradoQueryOutputPort {

    PaginatedResult<JuradoVigenteReadModel> consultarVigentes(JuradoVigenteCriteria criteria);
}
