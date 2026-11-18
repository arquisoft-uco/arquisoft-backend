package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoDocumentacionDomain {

    private UUID documentacion;
    private EstadoDocumentacion estado;

    private CambioEstadoDocumentacionDomain() {}

    public static CambioEstadoDocumentacionDomain crear(UUID documentacion, String estado) {
        var cambio = new CambioEstadoDocumentacionDomain();
        var result = new ValidationResult();

        cambio.setDocumentacion(documentacion, result);
        cambio.setEstado(estado, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setDocumentacion(UUID documentacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.Documentacion.DOCUMENTACION,
                BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO, result)) {
            return;
        }
        this.documentacion = documentacion;
    }

    private void setEstado(String estado, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estado);
        if (!ValidatorTexto.noEnBlanco(recortado,
                BibliotecaFields.Documentacion.ESTADO,
                BibliotecaCodes.Documentacion.ESTADO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoDocumentacion.esValido(recortado)) {
            result.agregarError(
                    BibliotecaFields.Documentacion.ESTADO,
                    BibliotecaCodes.Documentacion.ESTADO_INVALIDO,
                    Mensajes.formatear(DocumentacionKey.ERROR_ESTADO_INVALIDO));
            return;
        }
        this.estado = EstadoDocumentacion.desde(recortado);
    }

    public UUID getDocumentacion() {
        return documentacion;
    }

    public EstadoDocumentacion getEstado() {
        return estado;
    }
}
