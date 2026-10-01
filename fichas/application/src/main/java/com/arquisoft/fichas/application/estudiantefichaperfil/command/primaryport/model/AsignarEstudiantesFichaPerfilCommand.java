package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilCommand(
        UUID fichaPerfil,
        List<UUID> estudiantes
) {

    public AsignarEstudiantesFichaPerfilCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static AsignarEstudiantesFichaPerfilCommand crear(UUID fichaPerfil, List<String> estudiantes) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        List<String> lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (ValidatorColeccion.noVacia(lista,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result)) {
            ValidatorColeccion.tamanioMaximo(lista, FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                    FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                    FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO, result);
            lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                    FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                    FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result));
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfil, lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
