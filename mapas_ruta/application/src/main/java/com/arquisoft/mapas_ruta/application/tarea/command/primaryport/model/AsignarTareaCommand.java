package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AsignarTareaCommand(
        UUID mapaRuta,
        String descripcion,
        UUID asesor
) {

    public static AsignarTareaCommand crear(UUID mapaRuta, String descripcion, String asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.Tarea.MAPA_RUTA,
                MapasRutaCodes.Tarea.MAPA_RUTA_REQUERIDO, result);

        ValidatorTexto.noEnBlanco(descripcion,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA, result);

        if (ValidatorTexto.noEnBlanco(asesor,
                MapasRutaFields.Tarea.ASESOR,
                MapasRutaCodes.Tarea.ASESOR_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(asesor,
                    MapasRutaFields.Tarea.ASESOR,
                    MapasRutaCodes.Tarea.ASESOR_INVALIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarTareaCommand(mapaRuta, descripcion, UtilUUID.generarUUIDDesdeTexto(asesor));
    }
}
