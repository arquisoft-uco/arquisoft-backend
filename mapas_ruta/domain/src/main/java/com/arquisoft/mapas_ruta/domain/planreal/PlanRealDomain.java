package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class PlanRealDomain {

    private UUID id;
    private UUID tarea;
    private int tiempoRealDias;
    private String descripcionReal;

    private PlanRealDomain() {}

    public static PlanRealDomain crear(UUID tarea, Instant creadoEnPlanEstimado, Instant finalizadaEn,
                                       String descripcionReal) {
        var planReal = new PlanRealDomain();
        var result = new ValidationResult();

        planReal.setId();
        planReal.setTarea(tarea, result);
        planReal.setTiempoRealDias(creadoEnPlanEstimado, finalizadaEn, result);
        planReal.setDescripcionReal(descripcionReal, result);

        result.lanzarSiTieneErrores();
        return planReal;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanReal.TAREA,
                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    private void setTiempoRealDias(Instant creadoEn, Instant finalizadaEn, ValidationResult result) {
        var creadoEnPresente = ValidatorObjeto.noNulo(creadoEn,
                MapasRutaFields.PlanReal.CREADO_EN,
                MapasRutaCodes.PlanReal.PLAN_REAL_CREADO_EN_REQUERIDO, result);
        var finalizadaEnPresente = ValidatorObjeto.noNulo(finalizadaEn,
                MapasRutaFields.PlanReal.FINALIZADA_EN,
                MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA, result);
        if (!creadoEnPresente || !finalizadaEnPresente) {
            return;
        }
        var transcurrido = Duration.between(creadoEn, finalizadaEn);
        var diasCompletos = transcurrido.toDays();
        var hayResto = !transcurrido.minusDays(diasCompletos).isZero();
        var dias = hayResto ? diasCompletos + 1 : diasCompletos;
        this.tiempoRealDias = (int) Math.max(MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN, dias);
    }

    private void setDescripcionReal(String descripcionReal, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcionReal);
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX,
                MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcionReal = recortada;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTarea() {
        return tarea;
    }

    public int getTiempoRealDias() {
        return tiempoRealDias;
    }

    public String getDescripcionReal() {
        return UtilObjeto.esNulo(descripcionReal) ? UtilTexto.VACIO : descripcionReal;
    }
}
