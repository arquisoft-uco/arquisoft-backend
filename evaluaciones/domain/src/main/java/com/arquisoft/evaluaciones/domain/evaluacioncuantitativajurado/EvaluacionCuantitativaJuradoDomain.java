package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionCuantitativaJuradoDomain {

    public static final EvaluacionCuantitativaJuradoDomain VACIO = new EvaluacionCuantitativaJuradoDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            0);

    private UUID id;
    private UUID evaluacionJurado;
    private UUID item;
    private Integer puntaje;

    private EvaluacionCuantitativaJuradoDomain() {}

    private EvaluacionCuantitativaJuradoDomain(UUID id, UUID evaluacionJurado, UUID item, Integer puntaje) {
        this.id = id;
        this.evaluacionJurado = evaluacionJurado;
        this.item = item;
        this.puntaje = puntaje;
    }

    public static EvaluacionCuantitativaJuradoDomain crear(UUID evaluacionJurado, UUID item, Integer puntaje) {
        var evaluacionCuantitativa = new EvaluacionCuantitativaJuradoDomain();
        var result = new ValidationResult();

        evaluacionCuantitativa.setId();
        evaluacionCuantitativa.setEvaluacionJurado(evaluacionJurado, result);
        evaluacionCuantitativa.setItem(item, result);
        evaluacionCuantitativa.setPuntaje(puntaje, result);

        result.lanzarSiTieneErrores();
        return evaluacionCuantitativa;
    }

    public static EvaluacionCuantitativaJuradoDomain reconstruir(
            UUID id, UUID evaluacionJurado, UUID item, Integer puntaje) {
        return new EvaluacionCuantitativaJuradoDomain(id, evaluacionJurado, item, puntaje);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                EvaluacionesFields.EvaluacionCuantitativaJurado.ITEM,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setPuntaje(Integer puntaje, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(puntaje,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(puntaje,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO, result)) {
            return;
        }
        this.puntaje = puntaje;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
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
