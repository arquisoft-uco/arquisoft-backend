package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;

public record AsignarArtefactoCommand(
        UUID proyectoGrado,
        UUID asesor,
        UUID versionRepositorioArtefacto,
        List<UUID> estudiantes
) {

    public AsignarArtefactoCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static AsignarArtefactoCommand crear(
            String proyectoGrado, UUID asesor, String versionRepositorioArtefacto, List<String> estudiantes) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);
        ValidatorUUID.uuidValido(versionRepositorioArtefacto,
                ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA, result);
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO, result));

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarArtefactoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                asesor,
                UtilUUID.generarUUIDDesdeTexto(versionRepositorioArtefacto),
                lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
