package com.arquisoft.mapas_ruta.domain.planestimado;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class PlanEstimadoDomain {

    private UUID id;
    private UUID tarea;
    private int tiempoEstimadoDias;
    private String descripcionEstimado;

    private PlanEstimadoDomain() {}

    public static PlanEstimadoDomain crear(UUID tarea, Integer tiempoEstimadoDias, String descripcionEstimado) {
        var planEstimado = new PlanEstimadoDomain();
        var result = new ValidationResult();

        planEstimado.setId();
        planEstimado.setTarea(tarea, result);
        planEstimado.setTiempoEstimadoDias(tiempoEstimadoDias, result);
        planEstimado.setDescripcionEstimado(descripcionEstimado, result);

        result.lanzarSiTieneErrores();
        return planEstimado;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanEstimado.TAREA,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    private void setTiempoEstimadoDias(Integer tiempoEstimadoDias, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tiempoEstimadoDias,
                MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(tiempoEstimadoDias, MapasRutaLimits.PlanEstimado.TIEMPO_ESTIMADO_DIAS_MIN,
                MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_INVALIDO, result)) {
            return;
        }
        this.tiempoEstimadoDias = tiempoEstimadoDias;
    }

    private void setDescripcionEstimado(String descripcionEstimado, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcionEstimado);
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX,
                MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcionEstimado = recortada;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTarea() {
        return tarea;
    }

    public int getTiempoEstimadoDias() {
        return tiempoEstimadoDias;
    }

    public String getDescripcionEstimado() {
        return UtilObjeto.esNulo(descripcionEstimado) ? UtilTexto.VACIO : descripcionEstimado;
    }
}
