package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;

public interface RegistrarEvaluacionesCuantitativasJuradoValidator {

    void validar(
            ExistenciaEvaluacionJurado existencia,
            PropiedadEvaluacionJurado propiedad,
            ExistenciaItemsCuantitativosJurado items,
            PuntajesVsValoresMaximosJurado puntajes,
            DisponibilidadEvaluacionesCuantitativasJurado disponibilidad);
}
