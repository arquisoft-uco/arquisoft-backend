package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record GenerarPaqueteEntregableCommand(
        UUID entregableProyectoGrado,
        UUID proyectoGrado,
        int versionEntregable
) {

    private static final int VERSION_MINIMA = 1;

    public static GenerarPaqueteEntregableCommand crear(
            String entregableProyectoGrado, String proyectoGrado, Integer versionEntregable) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(entregableProyectoGrado,
                ArtefactosFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.PROYECTO_GRADO_REQUERIDO, result);
        if (ValidatorObjeto.noNulo(versionEntregable,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                    ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                    ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result);
        }
        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarPaqueteEntregableCommand(
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado),
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                versionEntregable);
    }
}
