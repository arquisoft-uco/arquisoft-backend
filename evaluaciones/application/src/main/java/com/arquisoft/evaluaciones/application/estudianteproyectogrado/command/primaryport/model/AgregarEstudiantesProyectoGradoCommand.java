package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AgregarEstudiantesProyectoGradoCommand(
        UUID proyectoGrado,
        List<UUID> estudiantes,
        Instant ocurridoEn
) {
    public static AgregarEstudiantesProyectoGradoCommand crear(
            String proyectoGrado, List<String> estudiantes, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorUUID.uuidValido(proyectoGrado, EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (ValidatorColeccion.noVacia(lista, EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTES,
                EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                    EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE,
                    EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result));
            ValidatorColeccion.sinDuplicados(lista, EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTES,
                    EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTE_DUPLICADO, result);
        }
        ValidatorObjeto.noNulo(ocurridoEn, EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN,
                EvaluacionesCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstudiantesProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList(),
                ocurridoEn);
    }
}
