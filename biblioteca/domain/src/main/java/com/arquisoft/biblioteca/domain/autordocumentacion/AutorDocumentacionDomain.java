package com.arquisoft.biblioteca.domain.autordocumentacion;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AutorDocumentacionDomain {

    private UUID id;
    private UUID autor;
    private UUID documentacion;

    private AutorDocumentacionDomain() {}

    public static AutorDocumentacionDomain crear(UUID autor, UUID documentacion) {
        var autorDocumentacion = new AutorDocumentacionDomain();
        var result = new ValidationResult();

        autorDocumentacion.setId();
        autorDocumentacion.setAutor(autor, result);
        autorDocumentacion.setDocumentacion(documentacion, result);

        result.lanzarSiTieneErrores();
        return autorDocumentacion;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setAutor(UUID autor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(autor,
                BibliotecaFields.AutorDocumentacion.AUTOR,
                BibliotecaCodes.AutorDocumentacion.AUTOR_REQUERIDO, result)) {
            return;
        }
        this.autor = autor;
    }

    private void setDocumentacion(UUID documentacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.AutorDocumentacion.DOCUMENTACION,
                BibliotecaCodes.AutorDocumentacion.DOCUMENTACION_ID_REQUERIDO, result)) {
            return;
        }
        this.documentacion = documentacion;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAutor() {
        return autor;
    }

    public UUID getDocumentacion() {
        return documentacion;
    }
}
