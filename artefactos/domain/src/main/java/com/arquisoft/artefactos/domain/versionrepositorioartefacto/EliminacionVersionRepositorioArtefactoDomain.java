package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionVersionRepositorioArtefactoDomain {

    private UUID version;

    private EliminacionVersionRepositorioArtefactoDomain() {}

    public static EliminacionVersionRepositorioArtefactoDomain crear(UUID version) {
        var eliminacion = new EliminacionVersionRepositorioArtefactoDomain();
        var result = new ValidationResult();

        eliminacion.setVersion(version, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
    }

    private void setVersion(UUID version, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(version,
                ArtefactosFields.VersionRepositorioArtefacto.ID,
                ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.version = version;
    }

    public UUID getVersion() {
        return version;
    }
}
