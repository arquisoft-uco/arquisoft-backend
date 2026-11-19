package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.LocalDate;

public record DetectarPlazosVencidosMapaRutaCommand(LocalDate hoy) {

    public static DetectarPlazosVencidosMapaRutaCommand crear(LocalDate hoy) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(hoy,
                MapasRutaFields.MapaRuta.HOY,
                MapasRutaCodes.MapaRuta.HOY_REQUERIDA, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new DetectarPlazosVencidosMapaRutaCommand(hoy);
    }
}
