package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record RegistrarObservacionCommand(
        UUID revisionAsesor,
        String descripcion,
        UUID asesor
) {

    public static RegistrarObservacionCommand crear(UUID revisionAsesor, String descripcion, UUID asesor) {
        var result = new ValidationResult();
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.Observacion.REVISION_ASESOR,
                ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Observacion.ASESOR,
                ArtefactosCodes.Observacion.ASESOR_REQUERIDO, result);
        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                ArtefactosFields.Observacion.DESCRIPCION,
                ArtefactosCodes.Observacion.DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada,
                    ArtefactosLimits.Observacion.DESCRIPCION_MAX,
                    ArtefactosFields.Observacion.DESCRIPCION,
                    ArtefactosCodes.Observacion.DESCRIPCION_LONGITUD_EXCEDIDA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarObservacionCommand(revisionAsesor, descripcionRecortada, asesor);
    }
}
