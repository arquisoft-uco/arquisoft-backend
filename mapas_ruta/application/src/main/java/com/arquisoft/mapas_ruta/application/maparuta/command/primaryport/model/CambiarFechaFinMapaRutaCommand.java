package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorFecha;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.LocalDate;
import java.util.UUID;

public record CambiarFechaFinMapaRutaCommand(
        UUID mapaRuta,
        UUID coordinador,
        LocalDate fechaFin
) {

    public static CambiarFechaFinMapaRutaCommand crear(UUID mapaRuta, UUID coordinador, String fechaFin) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.MapaRuta.MAPA_RUTA,
                MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.MapaRuta.COORDINADOR,
                MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(fechaFin,
                MapasRutaFields.MapaRuta.FECHA_FIN,
                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA, result)) {
            ValidatorFecha.fechaValida(fechaFin,
                    MapasRutaFields.MapaRuta.FECHA_FIN,
                    MapasRutaCodes.MapaRuta.FECHA_FIN_INVALIDA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarFechaFinMapaRutaCommand(
                mapaRuta,
                coordinador,
                UtilFecha.parsearFechaDesdeTexto(fechaFin));
    }
}
