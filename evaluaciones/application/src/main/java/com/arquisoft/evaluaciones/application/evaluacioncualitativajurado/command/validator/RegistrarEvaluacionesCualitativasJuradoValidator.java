package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;

public interface RegistrarEvaluacionesCualitativasJuradoValidator {

    void validarExistencia(ExistenciaEvaluacionJurado existencia);

    void validarContenido(
            ExistenciaItemsCualitativosJurado items,
            ExistenciaCriteriosCualitativosJurado criterios,
            DisponibilidadEvaluacionesCualitativasJurado disponibilidad);
}
