package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

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

public record AsignarArtefactosCommand(
        UUID proyectoGrado,
        UUID asesor,
        List<UUID> versionesRepositorioArtefacto
) {

    public AsignarArtefactosCommand {
        versionesRepositorioArtefacto = UtilColeccion.aplicarPorDefecto(versionesRepositorioArtefacto);
    }

    public static AsignarArtefactosCommand crear(String proyectoGrado, UUID asesor, List<String> versiones) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);
        var versionesConvertidas = validarVersiones(versiones, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarArtefactosCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), asesor, versionesConvertidas);
    }

    private static List<UUID> validarVersiones(List<String> versiones, ValidationResult result) {
        var convertidas = new ArrayList<UUID>();
        var lista = UtilColeccion.aplicarPorDefecto(versiones);
        if (!ValidatorColeccion.noVacia(lista,
                ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.Artefacto.VERSIONES_REQUERIDAS, result)) {
            return convertidas;
        }
        var todasValidas = true;
        for (var version : lista) {
            if (ValidatorUUID.uuidValido(version,
                    ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO,
                    ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA, result)) {
                convertidas.add(UtilUUID.generarUUIDDesdeTexto(version));
            } else {
                todasValidas = false;
            }
        }
        if (todasValidas) {
            ValidatorColeccion.sinDuplicados(convertidas,
                    ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO,
                    ArtefactosCodes.Artefacto.VERSIONES_DUPLICADAS, result);
        }
        return convertidas;
    }
}
