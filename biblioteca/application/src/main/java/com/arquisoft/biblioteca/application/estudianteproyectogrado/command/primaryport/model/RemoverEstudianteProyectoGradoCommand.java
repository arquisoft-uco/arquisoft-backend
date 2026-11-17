package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
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

        if (ValidatorUUID.uuidValido(proyectoGrado, BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        ValidatorUUID.uuidValido(estudiante, BibliotecaFields.EstudianteProyectoGrado.AUTOR,
                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, BibliotecaFields.EstudianteProyectoGrado.OCURRIDO_EN,
                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), UtilUUID.generarUUIDDesdeTexto(estudiante),
                ocurridoEn);
    }
}
