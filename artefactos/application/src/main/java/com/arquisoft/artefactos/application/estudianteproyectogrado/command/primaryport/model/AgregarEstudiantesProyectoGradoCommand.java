package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AgregarEstudiantesProyectoGradoCommand(
        UUID proyectoGrado,
        List<UUID> estudiantes,
        Instant ocurridoEn
) {

    public AgregarEstudiantesProyectoGradoCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static AgregarEstudiantesProyectoGradoCommand crear(
            String proyectoGrado, List<String> estudiantes, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ArtefactosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        var estudiantesConvertidos = validarEstudiantes(estudiantes, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstudiantesProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), estudiantesConvertidos, ocurridoEn);
    }

    private static List<UUID> validarEstudiantes(List<String> estudiantes, ValidationResult result) {
        var convertidos = new ArrayList<UUID>();
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            return convertidos;
        }
        var todosValidos = true;
        for (var estudiante : lista) {
            if (ValidatorUUID.uuidValido(estudiante,
                    ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result)) {
                convertidos.add(UtilUUID.generarUUIDDesdeTexto(estudiante));
            } else {
                todosValidos = false;
            }
        }
        if (todosValidos) {
            ValidatorColeccion.sinDuplicados(convertidos,
                    ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_DUPLICADOS, result);
        }
        return convertidos;
    }
}
