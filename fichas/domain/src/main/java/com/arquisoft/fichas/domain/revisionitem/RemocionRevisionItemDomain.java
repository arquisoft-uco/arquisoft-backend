package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionRevisionItemDomain {

    private UUID revisionItem;
    private UUID asesorFicha;

    private RemocionRevisionItemDomain() {}

    public static RemocionRevisionItemDomain crear(UUID revisionItem, UUID asesorFicha) {
        var remocion = new RemocionRevisionItemDomain();
        var result = new ValidationResult();

        remocion.setRevisionItem(revisionItem, result);
        remocion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setRevisionItem(UUID revisionItem, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionItem,
                FichasFields.RevisionItem.REVISION_ITEM,
                FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO, result)) {
            return;
        }
        this.revisionItem = revisionItem;
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public UUID getRevisionItem() {
        return revisionItem;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
