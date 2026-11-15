package com.arquisoft.biblioteca.application.documentacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoDocumentacionCommand(UUID documentacion, String estado) {

    public static CambiarEstadoDocumentacionCommand crear(UUID documentacion, String estado) {
        var result = new ValidationResult();
        var estadoRecortado = UtilTexto.aplicarTrim(estado);

        ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.Documentacion.DOCUMENTACION,
                BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoRecortado,
                BibliotecaFields.Documentacion.ESTADO,
                BibliotecaCodes.Documentacion.ESTADO_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoDocumentacionCommand(documentacion, estadoRecortado);
    }
}
