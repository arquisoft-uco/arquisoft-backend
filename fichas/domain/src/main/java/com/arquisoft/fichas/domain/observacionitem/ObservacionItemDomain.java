package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ObservacionItemDomain {

    private UUID id;
    private UUID revisionItem;
    private String observacion;
    private EstadoObservacionRevision estadoObservacionRevision;

    private ObservacionItemDomain() {}

    public static ObservacionItemDomain crear(UUID revisionItem, String observacion) {
        var observacionItem = new ObservacionItemDomain();
        var result = new ValidationResult();

        observacionItem.setId();
        observacionItem.setRevisionItem(revisionItem, result);
        observacionItem.setObservacion(observacion, result);
        observacionItem.setEstadoObservacionRevisionInicial();

        result.lanzarSiTieneErrores();
        return observacionItem;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setRevisionItem(UUID revisionItem, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionItem,
                FichasFields.ObservacionItem.REVISION_ITEM,
                FichasCodes.ObservacionItem.REVISION_ITEM_REQUERIDO, result)) {
            return;
        }
        this.revisionItem = revisionItem;
    }

    private void setObservacion(String observacion, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(observacion,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(observacion, FichasLimits.ObservacionItem.OBSERVACION_MAX,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setEstadoObservacionRevisionInicial() {
        this.estadoObservacionRevision = EstadoObservacionRevision.PENDIENTE;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRevisionItem() {
        return revisionItem;
    }

    public String getObservacion() {
        return observacion;
    }

    public EstadoObservacionRevision getEstadoObservacionRevision() {
        return estadoObservacionRevision;
    }
}
