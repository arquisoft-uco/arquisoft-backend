package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionCuantitativaAsesorDomain {

    public static final EvaluacionCuantitativaAsesorDomain VACIO = new EvaluacionCuantitativaAsesorDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilNumero.CERO);

    private UUID id;
    private UUID evaluacionAsesor;
    private UUID item;
    private Integer puntaje;

    private EvaluacionCuantitativaAsesorDomain() {}

    private EvaluacionCuantitativaAsesorDomain(UUID id, UUID evaluacionAsesor, UUID item, Integer puntaje) {
        this.id = id;
        this.evaluacionAsesor = evaluacionAsesor;
        this.item = item;
        this.puntaje = puntaje;
    }

    public static EvaluacionCuantitativaAsesorDomain crear(UUID evaluacionAsesor, UUID item, Integer puntaje) {
        var evaluacionCuantitativa = new EvaluacionCuantitativaAsesorDomain();
        var result = new ValidationResult();

        evaluacionCuantitativa.setId();
        evaluacionCuantitativa.setEvaluacionAsesor(evaluacionAsesor, result);
        evaluacionCuantitativa.setItem(item, result);
        evaluacionCuantitativa.setPuntaje(puntaje, result);

        result.lanzarSiTieneErrores();
        return evaluacionCuantitativa;
    }

    public static EvaluacionCuantitativaAsesorDomain reconstruir(
            UUID id, UUID evaluacionAsesor, UUID item, Integer puntaje) {
        return new EvaluacionCuantitativaAsesorDomain(id, evaluacionAsesor, item, puntaje);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionAsesor(UUID evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ITEM,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setPuntaje(Integer puntaje, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(puntaje,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(puntaje,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MIN,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO, result)) {
            return;
        }
        this.puntaje = puntaje;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public UUID getItem() {
        return item;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
