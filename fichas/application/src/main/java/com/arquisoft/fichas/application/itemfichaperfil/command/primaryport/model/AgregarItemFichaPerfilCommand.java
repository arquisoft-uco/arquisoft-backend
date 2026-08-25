package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record AgregarItemFichaPerfilCommand(
        UUID fichaPerfil,
        String tipoItem,
        String contenido,
        UUID estudiante
) {

    public AgregarItemFichaPerfilCommand {
        tipoItem = UtilTexto.aplicarTrim(tipoItem);
        contenido = UtilTexto.aplicarTrim(contenido);
    }

    public static AgregarItemFichaPerfilCommand crear(
            UUID fichaPerfil, String tipoItem, String contenido, UUID estudiante) {

        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(tipoItem,
                FichasFields.ItemFichaPerfil.TIPO_ITEM,
                FichasCodes.ItemFichaPerfil.TIPO_ITEM_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(contenido, FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                    FichasFields.ItemFichaPerfil.CONTENIDO,
                    FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarItemFichaPerfilCommand(fichaPerfil, tipoItem, contenido, estudiante);
    }
}
