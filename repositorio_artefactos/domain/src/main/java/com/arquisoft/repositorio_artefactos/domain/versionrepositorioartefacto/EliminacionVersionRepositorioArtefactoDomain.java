package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
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
                RepositorioArtefactosFields.VersionRepositorioArtefacto.VERSION,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_REQUERIDA, result)) {
            return;
        }
        this.version = version;
    }

    public UUID getVersion() {
        return version;
    }
}
