package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorNoAdmiteDecisionObservacionException extends DomainException {

    public RevisionAsesorNoAdmiteDecisionObservacionException(UUID revisionAsesor, EstadoRevisionAsesor estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.NO_ADMITE_DECISION_OBSERVACION,
                Mensajes.obtener(RevisionAsesorKey.ERROR_NO_ADMITE_DECISION_OBSERVACION),
                Mensajes.formatear(RevisionAsesorKey.ERROR_NO_ADMITE_DECISION_OBSERVACION_DETALLE,
                        revisionAsesor, estado.getId())
        ));
    }
}
