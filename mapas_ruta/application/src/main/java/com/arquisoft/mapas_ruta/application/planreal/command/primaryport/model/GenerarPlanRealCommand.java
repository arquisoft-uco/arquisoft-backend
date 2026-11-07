package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record GenerarPlanRealCommand(UUID tarea, Instant finalizadaEn) {

    public static GenerarPlanRealCommand crear(String tarea, Instant finalizadaEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(tarea, MapasRutaFields.PlanReal.TAREA,
                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_INVALIDA, result);
        ValidatorObjeto.noNulo(finalizadaEn, MapasRutaFields.PlanReal.FINALIZADA_EN,
                MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarPlanRealCommand(UtilUUID.generarUUIDDesdeTexto(tarea), finalizadaEn);
    }
}
