package com.arquisoft.artefactos.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarArtefactosSinAvanceQuery(
        UUID asesor,
        int diasSinAvance,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarArtefactosSinAvanceQuery crear(
            UUID asesor, Integer diasSinAvance, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);

        var diasPresentes = ValidatorObjeto.noNulo(diasSinAvance,
                ArtefactosFields.Artefacto.DIAS_SIN_AVANCE,
                ArtefactosCodes.Artefacto.DIAS_SIN_AVANCE_REQUERIDO, result);

        if (diasPresentes) {
            ValidatorNumero.valorEntre(diasSinAvance,
                    ArtefactosLimits.Artefacto.DIAS_SIN_AVANCE_MINIMO,
                    ArtefactosLimits.Artefacto.DIAS_SIN_AVANCE_MAXIMO,
                    ArtefactosFields.Artefacto.DIAS_SIN_AVANCE,
                    ArtefactosCodes.Artefacto.DIAS_SIN_AVANCE_FUERA_DE_RANGO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarArtefactosSinAvanceQuery(asesor, diasSinAvance, criterio);
    }
}
