package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;

public final class RevisionAnteriorNoPermiteEliminarVersionException extends DomainException {

    public RevisionAnteriorNoPermiteEliminarVersionException(int numeroVersion, RevisionAsesorDomain revision) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.REVISION_ANTERIOR_NO_PERMITE_ELIMINAR,
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_REVISION_ANTERIOR_NO_PERMITE,
                        numeroVersion, revision.getEstado().getNombre()),
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_REVISION_ANTERIOR_NO_PERMITE_DETALLE,
                        revision.getId(), revision.getEstado().getId())
        ));
    }
}
