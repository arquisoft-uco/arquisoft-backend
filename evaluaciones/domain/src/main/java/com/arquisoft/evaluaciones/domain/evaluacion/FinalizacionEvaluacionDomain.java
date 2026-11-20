package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.resultadoevaluacion.ResultadoEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public final class FinalizacionEvaluacionDomain {

    private ConsolidacionNotasEvaluacionDomain consolidacion;
    private BigDecimal notaFinal;
    private ResultadoEvaluacion resultado;

    private FinalizacionEvaluacionDomain() {}

    public static FinalizacionEvaluacionDomain crear(
            ConsolidacionNotasEvaluacionDomain consolidacion, boolean hayObservaciones) {
        var finalizacion = new FinalizacionEvaluacionDomain();
        var result = new ValidationResult();

        finalizacion.setConsolidacion(consolidacion, result);

        result.lanzarSiTieneErrores();
        finalizacion.setNotaFinal();
        finalizacion.setResultado(hayObservaciones);
        return finalizacion;
    }

    private void setConsolidacion(ConsolidacionNotasEvaluacionDomain consolidacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(consolidacion,
                EvaluacionesFields.Evaluacion.EVALUACION,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.EVALUACION_REQUERIDA, result)) {
            return;
        }
        this.consolidacion = consolidacion;
    }

    private void setNotaFinal() {
        this.notaFinal = consolidacion.getPesoAsesor().multiply(consolidacion.getNotaAsesor())
                .add(consolidacion.getPesoJurados().multiply(consolidacion.getNotaJurados()))
                .setScale(EvaluacionesLimits.Evaluacion.DECIMALES_NOTA, RoundingMode.HALF_UP);
    }

    private void setResultado(boolean hayObservaciones) {
        var notaMinima = BigDecimal.valueOf(EvaluacionesLimits.Evaluacion.NOTA_MINIMA_APROBACION);
        if (notaFinal.compareTo(notaMinima) < 0) {
            this.resultado = ResultadoEvaluacion.RECHAZADA;
            return;
        }
        this.resultado = hayObservaciones
                ? ResultadoEvaluacion.APROBADA_CON_OBSERVACIONES
                : ResultadoEvaluacion.APROBADA;
    }

    public ConsolidacionNotasEvaluacionDomain getConsolidacion() {
        return consolidacion;
    }

    public UUID getEvaluacion() {
        return consolidacion.getEvaluacion();
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }
}
