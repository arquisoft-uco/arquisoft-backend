package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;

import java.util.List;

public interface CriterioItemCualitativoAsesorQueryOutputPort {

    List<CriterioItemCualitativoAsesorReadModel> consultarTodos();
}
