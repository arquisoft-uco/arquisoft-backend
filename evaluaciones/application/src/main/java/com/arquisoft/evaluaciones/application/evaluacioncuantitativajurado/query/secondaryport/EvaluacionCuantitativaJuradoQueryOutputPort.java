package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoQueryOutputPort {

    List<EvaluacionCuantitativaJuradoReadModel> consultar(EvaluacionCuantitativaJuradoCriteria criteria);

    boolean existePorId(UUID evaluacionCuantitativaJurado);
}
