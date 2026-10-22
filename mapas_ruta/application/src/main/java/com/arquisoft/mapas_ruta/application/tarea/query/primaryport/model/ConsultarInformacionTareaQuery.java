package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarInformacionTareaQuery(UUID tarea) {

    public static ConsultarInformacionTareaQuery crear(UUID tarea) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarInformacionTareaQuery(tarea);
    }
}
