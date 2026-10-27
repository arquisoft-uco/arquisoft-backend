package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesArtefactoCommand(
        UUID artefacto,
        UUID asesor,
        List<UUID> estudiantes
) {

    public AsignarEstudiantesArtefactoCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static AsignarEstudiantesArtefactoCommand crear(UUID artefacto, UUID asesor, List<String> estudiantes) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.EstudianteArtefacto.ASESOR,
                ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO, result);
        var estudiantesConvertidos = validarEstudiantes(estudiantes, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarEstudiantesArtefactoCommand(artefacto, asesor, estudiantesConvertidos);
    }

    private static List<UUID> validarEstudiantes(List<String> estudiantes, ValidationResult result) {
        var convertidos = new ArrayList<UUID>();
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS, result)) {
            return convertidos;
        }
        var todosValidos = true;
        for (var estudiante : lista) {
            if (ValidatorUUID.uuidValido(estudiante,
                    ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                    ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO, result)) {
                convertidos.add(UtilUUID.generarUUIDDesdeTexto(estudiante));
            } else {
                todosValidos = false;
            }
        }
        if (todosValidos) {
            ValidatorColeccion.sinDuplicados(convertidos,
                    ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                    ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_DUPLICADOS, result);
        }
        return convertidos;
    }
}
