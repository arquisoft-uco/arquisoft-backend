package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class RemoverItemFichaPerfilDomain {

    private UUID item;
    private UUID estudiante;

    private RemoverItemFichaPerfilDomain() {}

    public static RemoverItemFichaPerfilDomain crear(UUID item, UUID estudiante) {
        var transaccion = new RemoverItemFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setItem(item, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!DomainValidator.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM,
                FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!DomainValidator.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getItem() {
        return item;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
