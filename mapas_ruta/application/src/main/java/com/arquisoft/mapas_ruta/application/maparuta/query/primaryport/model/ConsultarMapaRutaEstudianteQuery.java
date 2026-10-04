package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarMapaRutaEstudianteQuery(UUID estudiante) {

    public static ConsultarMapaRutaEstudianteQuery crear(UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                MapasRutaFields.MapaRuta.ESTUDIANTE,
                MapasRutaCodes.MapaRuta.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarMapaRutaEstudianteQuery(estudiante);
    }
}
