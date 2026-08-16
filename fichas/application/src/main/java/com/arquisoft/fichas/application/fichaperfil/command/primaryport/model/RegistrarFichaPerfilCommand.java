package com.arquisoft.fichas.application.fichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;

public record RegistrarFichaPerfilCommand(
        String tituloProyecto,
        UUID asesorFicha,
        List<UUID> estudiantes
) {
    public RegistrarFichaPerfilCommand {
        tituloProyecto = UtilTexto.aplicarTrim(tituloProyecto);
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static RegistrarFichaPerfilCommand crear(
            String tituloProyecto, String asesorFicha, List<String> estudiantes) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(tituloProyecto, FichasLimits.FichaPerfil.TITULO_MAX,
                    FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        }

        if (ValidatorTexto.noEnBlanco(asesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(asesorFicha,
                    FichasFields.FichaPerfil.ASESOR_FICHA, FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result);
        }

        List<String> lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        ValidatorColeccion.tamanioMaximo(lista, FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                FichasFields.FichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO, result);
        lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result));

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarFichaPerfilCommand(
                tituloProyecto,
                UtilUUID.generarUUIDDesdeTexto(asesorFicha),
                lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
