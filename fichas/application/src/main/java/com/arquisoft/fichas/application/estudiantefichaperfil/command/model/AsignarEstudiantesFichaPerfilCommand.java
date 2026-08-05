package com.arquisoft.fichas.application.estudiantefichaperfil.command.model;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.util.UtilCollection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilCommand(
        UUID fichaPerfil,
        List<UUID> estudiantes
) {

    public AsignarEstudiantesFichaPerfilCommand {
        estudiantes = UtilCollection.applyDefault(estudiantes);
    }

    public static AsignarEstudiantesFichaPerfilCommand crear(UUID fichaPerfil, List<String> estudiantes) {
        var result = new ValidationResult();

        DomainValidator.noNulo(fichaPerfil,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);

        List<String> lista = UtilCollection.applyDefault(estudiantes);
        if (DomainValidator.noVacia(lista,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result)) {
            DomainValidator.tamanioMaximo(lista, FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                    FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                    FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO, result);
            lista.forEach(estudiante -> DomainValidator.uuidValido(estudiante,
                    FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                    FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result));
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfil, lista.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
