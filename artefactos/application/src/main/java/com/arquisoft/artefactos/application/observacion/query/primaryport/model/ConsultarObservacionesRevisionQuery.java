package com.arquisoft.artefactos.application.observacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesRevisionQuery(UUID revisionAsesor) {

    public static ConsultarObservacionesRevisionQuery crear(UUID revisionAsesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.Observacion.REVISION_ASESOR,
                ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesRevisionQuery(revisionAsesor);
    }
}
