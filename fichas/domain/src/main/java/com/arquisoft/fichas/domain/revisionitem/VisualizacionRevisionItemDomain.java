package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class VisualizacionRevisionItemDomain {

    private UUID revisionItem;
    private UUID estudiante;

    private VisualizacionRevisionItemDomain() {}

    public static VisualizacionRevisionItemDomain crear(UUID revisionItem, UUID estudiante) {
        var visualizacion = new VisualizacionRevisionItemDomain();
        var result = new ValidationResult();

        visualizacion.setRevisionItem(revisionItem, result);
        visualizacion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return visualizacion;
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
