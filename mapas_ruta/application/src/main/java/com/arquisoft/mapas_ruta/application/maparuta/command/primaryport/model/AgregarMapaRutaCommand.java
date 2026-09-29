package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorFecha;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.LocalDate;
import java.util.UUID;

public record AgregarMapaRutaCommand(
        UUID proyectoGrado,
        UUID coordinador,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {

    public static AgregarMapaRutaCommand crear(
            String proyectoGrado, UUID coordinador, String fechaInicio, String fechaFin) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(proyectoGrado,
                MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(proyectoGrado,
                    MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                    MapasRutaCodes.MapaRuta.PROYECTO_GRADO_INVALIDO, result);
        }

        ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.MapaRuta.COORDINADOR,
                MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(fechaInicio,
                MapasRutaFields.MapaRuta.FECHA_INICIO,
                MapasRutaCodes.MapaRuta.FECHA_INICIO_REQUERIDA, result)) {
            ValidatorFecha.fechaValida(fechaInicio,
                    MapasRutaFields.MapaRuta.FECHA_INICIO,
                    MapasRutaCodes.MapaRuta.FECHA_INICIO_INVALIDA, result);
        }

        if (ValidatorTexto.noEnBlanco(fechaFin,
                MapasRutaFields.MapaRuta.FECHA_FIN,
                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA, result)) {
            ValidatorFecha.fechaValida(fechaFin,
                    MapasRutaFields.MapaRuta.FECHA_FIN,
                    MapasRutaCodes.MapaRuta.FECHA_FIN_INVALIDA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarMapaRutaCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                coordinador,
                UtilFecha.parsearFechaDesdeTexto(fechaInicio),
                UtilFecha.parsearFechaDesdeTexto(fechaFin));
    }
}
