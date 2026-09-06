package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionRevisionItemDomain {

    private UUID item;
    private EstadoRevision estadoRevision;
    private UUID asesorFicha;

    private ModificacionRevisionItemDomain() {}

    public static ModificacionRevisionItemDomain crear(UUID item, String estadoRevision, UUID asesorFicha) {
        var modificacion = new ModificacionRevisionItemDomain();
        var result = new ValidationResult();

        modificacion.setItem(item, result);
        modificacion.setEstadoRevision(estadoRevision, result);
        modificacion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                FichasFields.RevisionItem.ITEM,
                FichasCodes.RevisionItem.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setEstadoRevision(String estadoRevision, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(estadoRevision,
                FichasFields.RevisionItem.ESTADO_REVISION,
                FichasCodes.RevisionItem.ESTADO_REVISION_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(estadoRevision, FichasLimits.RevisionItem.ESTADO_MAX,
                FichasFields.RevisionItem.ESTADO_REVISION,
                FichasCodes.RevisionItem.ESTADO_REVISION_DEMASIADO_LARGO, result)) {
            return;
        }
        if (!EstadoRevision.esValido(estadoRevision)) {
            result.agregarError(
                    FichasFields.RevisionItem.ESTADO_REVISION,
                    FichasCodes.RevisionItem.ESTADO_REVISION_NO_ENCONTRADO,
                    Mensajes.formatear(RevisionItemKey.ERROR_ESTADO_NO_ENCONTRADO, estadoRevision));
            return;
        }
        this.estadoRevision = EstadoRevision.desde(estadoRevision);
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public UUID getItem() {
        return item;
    }

    public EstadoRevision getEstadoRevision() {
        return estadoRevision;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
