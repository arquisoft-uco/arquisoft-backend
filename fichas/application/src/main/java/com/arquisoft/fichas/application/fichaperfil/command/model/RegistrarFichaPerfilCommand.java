package com.arquisoft.fichas.application.fichaperfil.command.model;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.util.UtilCollection;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;
import java.util.UUID;

public record RegistrarFichaPerfilCommand(
        String tituloProyecto,
        UUID asesorFicha,
        List<UUID> estudiantes
) {
    public RegistrarFichaPerfilCommand {
        tituloProyecto = UtilText.applyTrim(tituloProyecto);
        estudiantes = UtilCollection.applyDefault(estudiantes);
    }

    public static RegistrarFichaPerfilCommand crear(
            String tituloProyecto, String asesorFicha, List<String> estudiantes) {
        var result = new ValidationResult();

        if (DomainValidator.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            DomainValidator.longitudMaxima(tituloProyecto, FichasLimits.FichaPerfil.TITULO_MAX,
                    FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        }

        if (DomainValidator.noEnBlanco(asesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            DomainValidator.uuidValido(asesorFicha,
                    FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result);
        }

        List<String> lista = UtilCollection.applyDefault(estudiantes);
        DomainValidator.tamanioMaximo(lista, FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                FichasFields.FichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO, result);
        lista.forEach(estudiante -> DomainValidator.uuidValido(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result));

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarFichaPerfilCommand(
                tituloProyecto,
                UtilUUID.generateUUIDFromString(asesorFicha),
                lista.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
