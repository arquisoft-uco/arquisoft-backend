package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;

import java.util.List;

public interface EvaluacionCualitativaJuradoQueryOutputPort {

    List<EvaluacionCualitativaJuradoReadModel> consultar(EvaluacionCualitativaJuradoCriteria criteria);
}
