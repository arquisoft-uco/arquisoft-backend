package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class RevisionItemDomain {

    private UUID id;
    private UUID item;
    private EstadoRevision estadoRevision;
    private Instant fechaCreacion;

    private RevisionItemDomain() {}

    public static RevisionItemDomain crear(UUID item) {
        var revisionItem = new RevisionItemDomain();
        var result = new ValidationResult();

        revisionItem.setId();
        revisionItem.setItem(item, result);
        revisionItem.setEstadoRevisionInicial();
        revisionItem.setFechaCreacion();

        result.lanzarSiTieneErrores();
        return revisionItem;
    }

    public static RevisionItemDomain crearConEstado(UUID item, String estadoRevision) {
        var revisionItem = new RevisionItemDomain();
        var result = new ValidationResult();

        revisionItem.setId();
        revisionItem.setItem(item, result);
        revisionItem.setEstadoRevision(estadoRevision, result);
        revisionItem.setFechaCreacion();

        result.lanzarSiTieneErrores();
        return revisionItem;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                FichasFields.RevisionItem.ITEM,
                FichasCodes.RevisionItem.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setEstadoRevisionInicial() {
        this.estadoRevision = EstadoRevision.NUEVA;
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

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
    }

    public UUID getId() {
        return id;
    }

    public UUID getItem() {
        return item;
    }

    public EstadoRevision getEstadoRevision() {
        return estadoRevision;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}
