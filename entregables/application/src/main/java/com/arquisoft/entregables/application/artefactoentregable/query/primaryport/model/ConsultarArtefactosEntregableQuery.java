package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarArtefactosEntregableQuery(UUID entregableProyectoGrado) {

    public static ConsultarArtefactosEntregableQuery crear(UUID entregableProyectoGrado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.EntregableProyectoGrado.ID,
                EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarArtefactosEntregableQuery(entregableProyectoGrado);
    }
}
