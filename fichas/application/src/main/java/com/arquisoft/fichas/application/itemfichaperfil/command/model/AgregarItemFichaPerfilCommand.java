package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public record AgregarItemFichaPerfilCommand(
        UUID fichaPerfil,
        String tipoItem,
        String contenido,
        UUID estudiante
) {

    public AgregarItemFichaPerfilCommand {
        tipoItem = UtilText.applyTrim(tipoItem);
        contenido = UtilText.applyTrim(contenido);
    }

    public static AgregarItemFichaPerfilCommand crear(
            UUID fichaPerfil, String tipoItem, String contenido, UUID estudiante) {

        var result = new ValidationResult();

        DomainValidator.noNulo(fichaPerfil,
                FichasFields.ItemFichaPerfil.FICHA_PERFIL,
                FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result);
        DomainValidator.noEnBlanco(tipoItem,
                FichasFields.ItemFichaPerfil.TIPO_ITEM,
                FichasCodes.ItemFichaPerfil.TIPO_ITEM_REQUERIDO, result);

        if (DomainValidator.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            DomainValidator.longitudMaxima(contenido, FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                    FichasFields.ItemFichaPerfil.CONTENIDO,
                    FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result);
        }

        DomainValidator.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarItemFichaPerfilCommand(fichaPerfil, tipoItem, contenido, estudiante);
    }
}
