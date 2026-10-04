package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;

import java.util.List;

public interface EvaluacionCuantitativaJuradoQueryOutputPort {

    List<EvaluacionCuantitativaJuradoReadModel> consultar(EvaluacionCuantitativaJuradoCriteria criteria);
}
