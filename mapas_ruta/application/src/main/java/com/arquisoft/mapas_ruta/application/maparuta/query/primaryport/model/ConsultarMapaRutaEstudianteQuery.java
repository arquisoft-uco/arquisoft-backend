package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarMapaRutaEstudianteQuery(UUID proyectoGrado) {

    public static ConsultarMapaRutaEstudianteQuery crear(UUID proyectoGrado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarMapaRutaEstudianteQuery(proyectoGrado);
    }
}
