package com.arquisoft.evaluaciones.application.entregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarEntregableCommand(UUID entregable, UUID proyectoGrado, String proyecto, Integer version) {

    public static AgregarEntregableCommand crear(
            String entregable, String proyectoGrado, String proyecto, Integer version) {
        var result = new ValidationResult();
        var proyectoRecortado = UtilTexto.aplicarTrim(proyecto);

        ValidatorUUID.uuidValido(entregable, EvaluacionesFields.Entregable.ID,
                EvaluacionesCodes.Entregable.ENTREGABLE_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado, EvaluacionesFields.Entregable.PROYECTO_GRADO,
                EvaluacionesCodes.Entregable.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(proyectoRecortado, EvaluacionesFields.Entregable.PROYECTO,
                EvaluacionesCodes.Entregable.PROYECTO_REQUERIDO, result);
        ValidatorObjeto.noNulo(version, EvaluacionesFields.Entregable.VERSION,
                EvaluacionesCodes.Entregable.VERSION_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEntregableCommand(
                UtilUUID.generarUUIDDesdeTexto(entregable),
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                proyectoRecortado,
                version);
    }
}
