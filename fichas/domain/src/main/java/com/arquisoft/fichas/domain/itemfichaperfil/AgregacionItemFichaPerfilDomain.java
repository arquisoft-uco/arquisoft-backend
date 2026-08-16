package com.arquisoft.fichas.domain.itemfichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionItemFichaPerfilDomain {

    private ItemFichaPerfilDomain item;
    private UUID estudiante;

    private AgregacionItemFichaPerfilDomain() {}

    public static AgregacionItemFichaPerfilDomain crear(ItemFichaPerfilDomain item, UUID estudiante) {
        var transaccion = new AgregacionItemFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setItem(item, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setItem(ItemFichaPerfilDomain item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM,
                FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public ItemFichaPerfilDomain getItem() {
        return item;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
