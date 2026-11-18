package com.arquisoft.biblioteca.application.documentacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record RegistrarDocumentacionCommand(UUID entregableProyectoGrado, UUID bibliotecario) {

    public static RegistrarDocumentacionCommand crear(String entregableProyectoGrado, UUID bibliotecario) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(entregableProyectoGrado,
                BibliotecaFields.Documentacion.ENTREGABLE_PROYECTO_GRADO,
                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO, result);
        ValidatorObjeto.noNulo(bibliotecario,
                BibliotecaFields.Documentacion.BIBLIOTECARIO,
                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarDocumentacionCommand(UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado), bibliotecario);
    }
}
