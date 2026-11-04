package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionTareaDomain {

    private UUID tarea;

    private RemocionTareaDomain() {}

    public static RemocionTareaDomain crear(UUID tarea) {
        var remocion = new RemocionTareaDomain();
        var result = new ValidationResult();

        remocion.setTarea(tarea, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    public UUID getTarea() {
        return tarea;
    }
}
