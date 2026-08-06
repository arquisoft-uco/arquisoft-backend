package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class AgregarItemFichaPerfilDomain {

    private ItemFichaPerfilDomain item;
    private UUID estudiante;

    private AgregarItemFichaPerfilDomain() {}

    public static AgregarItemFichaPerfilDomain crear(
            UUID fichaPerfil, String tipoItem, String contenido, UUID estudiante) {
        var transaccion = new AgregarItemFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setItem(fichaPerfil, tipoItem, contenido, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setItem(UUID fichaPerfil, String tipoItem, String contenido, ValidationResult result) {
        this.item = ItemFichaPerfilDomain.crear(fichaPerfil, tipoItem, contenido, result);
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!DomainValidator.noNulo(estudiante,
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
