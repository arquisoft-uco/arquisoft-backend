package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionRevisionItemDomain {

    private RevisionItemDomain revisionItem;
    private UUID asesorFicha;

    private AgregacionRevisionItemDomain() {}

    public static AgregacionRevisionItemDomain crear(RevisionItemDomain revisionItem, UUID asesorFicha) {
        var agregacion = new AgregacionRevisionItemDomain();
        var result = new ValidationResult();

        agregacion.setRevisionItem(revisionItem, result);
        agregacion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setRevisionItem(RevisionItemDomain revisionItem, ValidationResult result) {
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

    public RevisionItemDomain getRevisionItem() {
        return revisionItem;
    }

    public UUID getItem() {
        return revisionItem.getItem();
    }

    public EstadoRevision getEstadoRevision() {
        return revisionItem.getEstadoRevision();
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
