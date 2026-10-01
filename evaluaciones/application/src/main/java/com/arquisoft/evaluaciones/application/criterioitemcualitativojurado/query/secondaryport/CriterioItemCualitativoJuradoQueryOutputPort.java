package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;

import java.util.List;

public interface CriterioItemCualitativoJuradoQueryOutputPort {

    List<CriterioItemCualitativoJuradoReadModel> consultarTodos();
}
