package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarMapasRutaCoordinadorQuery(
        UUID coordinador,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarMapasRutaCoordinadorQuery crear(
            UUID coordinador, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.MapaRuta.COORDINADOR,
                MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarMapasRutaCoordinadorQuery(coordinador, criterio);
    }
}
