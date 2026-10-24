package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ModificarInformacionTareaCommand(
        UUID tarea,
        String descripcion,
        UUID asesor
) {

    public static ModificarInformacionTareaCommand crear(UUID tarea, String descripcion, String asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result);

        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);
        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada, MapasRutaLimits.Tarea.DESCRIPCION_MAX,
                    MapasRutaFields.Tarea.DESCRIPCION,
                    MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA, result);
        }

        if (ValidatorTexto.noEnBlanco(asesor,
                MapasRutaFields.Tarea.ASESOR,
                MapasRutaCodes.Tarea.ASESOR_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(asesor,
                    MapasRutaFields.Tarea.ASESOR,
                    MapasRutaCodes.Tarea.ASESOR_INVALIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarInformacionTareaCommand(
                tarea, descripcionRecortada, UtilUUID.generarUUIDDesdeTexto(asesor));
    }
}
