package com.arquisoft.fichas.domain.itemfichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionItemFichaPerfilDomain {

    private UUID item;
    private String contenido;
    private UUID estudiante;

    private ModificacionItemFichaPerfilDomain() {}

    public static ModificacionItemFichaPerfilDomain crear(UUID item, String contenido, UUID estudiante) {
        var transaccion = new ModificacionItemFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setItem(item, result);
        transaccion.setContenido(contenido, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM,
                FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setContenido(String contenido, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(contenido,
                FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.contenido = UtilTexto.aplicarTrim(contenido);
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getItem() {
        return item;
    }

    public String getContenido() {
        return contenido;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
