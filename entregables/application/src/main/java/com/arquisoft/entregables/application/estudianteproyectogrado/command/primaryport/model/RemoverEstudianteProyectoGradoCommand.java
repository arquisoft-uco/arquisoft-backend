package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RemoverEstudianteProyectoGradoCommand(
        UUID proyectoGrado,
        UUID estudiante,
        Instant ocurridoEn
) {
    public static RemoverEstudianteProyectoGradoCommand crear(
            String proyectoGrado, String estudiante, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorUUID.uuidValido(proyectoGrado, EntregablesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(EntregablesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    EntregablesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        ValidatorUUID.uuidValido(estudiante, EntregablesFields.EstudianteProyectoGrado.ESTUDIANTE,
                EntregablesCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EntregablesFields.EstudianteProyectoGrado.OCURRIDO_EN,
                EntregablesCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), UtilUUID.generarUUIDDesdeTexto(estudiante),
                ocurridoEn);
    }
}
