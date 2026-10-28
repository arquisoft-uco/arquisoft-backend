package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado.EstadoEvaluacionAsignadaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CompletitudEvaluacionJuradoDomain {

    private UUID evaluacionJurado;
    private boolean completa;

    private CompletitudEvaluacionJuradoDomain() {}

    public static CompletitudEvaluacionJuradoDomain crear(UUID evaluacionJurado, boolean completa) {
        var accion = new CompletitudEvaluacionJuradoDomain();
        var result = new ValidationResult();

        accion.setEvaluacionJurado(evaluacionJurado, result);
        accion.setCompleta(completa);

        result.lanzarSiTieneErrores();
        return accion;
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionJurado.ID,
                EvaluacionesCodes.EvaluacionJurado.ID_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setCompleta(boolean completa) {
        this.completa = completa;
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public boolean isCompleta() {
        return completa;
    }

    public boolean requiereTransicion() {
        return completa;
    }

    public EstadoEvaluacionAsignadaJurado estadoDestino() {
        return EstadoEvaluacionAsignadaJurado.EVALUADA;
    }
}
