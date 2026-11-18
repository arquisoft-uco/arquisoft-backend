package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionDomain {

    private UUID id;
    private UUID entregable;
    private EstadoEvaluacion estado;

    private EvaluacionDomain() {}

    public static EvaluacionDomain crear(UUID entregable) {
        var evaluacion = new EvaluacionDomain();
        var result = new ValidationResult();

        evaluacion.setId();
        evaluacion.setEntregable(entregable, result);
        evaluacion.setEstadoInicial();

        result.lanzarSiTieneErrores();
        return evaluacion;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEntregable(UUID entregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregable,
                EvaluacionesFields.Evaluacion.ENTREGABLE,
                EvaluacionesCodes.Evaluacion.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregable = entregable;
    }

    private void setEstadoInicial() {
        this.estado = EstadoEvaluacion.PENDIENTE;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntregable() {
        return entregable;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }
}
