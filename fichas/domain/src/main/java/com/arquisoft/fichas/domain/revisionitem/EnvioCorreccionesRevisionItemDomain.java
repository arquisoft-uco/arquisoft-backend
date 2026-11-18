package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EnvioCorreccionesRevisionItemDomain {

    private UUID revisionItem;
    private UUID estudiante;

    private EnvioCorreccionesRevisionItemDomain() {}

    public static EnvioCorreccionesRevisionItemDomain crear(UUID revisionItem, UUID estudiante) {
        var envio = new EnvioCorreccionesRevisionItemDomain();
        var result = new ValidationResult();

        envio.setRevisionItem(revisionItem, result);
        envio.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return envio;
    }

    private void setRevisionItem(UUID revisionItem, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionItem,
                FichasFields.RevisionItem.REVISION_ITEM,
                FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO, result)) {
            return;
        }
        this.revisionItem = revisionItem;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                FichasFields.RevisionItem.ESTUDIANTE,
                FichasCodes.RevisionItem.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getRevisionItem() {
        return revisionItem;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
