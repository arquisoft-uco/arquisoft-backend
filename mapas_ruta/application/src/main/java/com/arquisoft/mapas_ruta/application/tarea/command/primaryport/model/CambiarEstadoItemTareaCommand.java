package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoItemTareaCommand(
        UUID tarea,
        String estadoItem
) {

    public static CambiarEstadoItemTareaCommand crear(UUID tarea, String estadoItem) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result);

        ValidatorTexto.noEnBlanco(estadoItem,
                MapasRutaFields.Tarea.ESTADO_ITEM,
                MapasRutaCodes.Tarea.ESTADO_ITEM_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoItemTareaCommand(tarea, estadoItem);
    }
}
