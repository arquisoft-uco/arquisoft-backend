package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarTareasMapaRutaEstudianteQuery(
        UUID mapaRuta,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarTareasMapaRutaEstudianteQuery crear(
            UUID mapaRuta, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.Tarea.MAPA_RUTA,
                MapasRutaCodes.Tarea.MAPA_RUTA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarTareasMapaRutaEstudianteQuery(mapaRuta, criterio);
    }
}
