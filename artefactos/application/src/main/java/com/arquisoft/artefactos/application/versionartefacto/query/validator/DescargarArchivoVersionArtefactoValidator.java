package com.arquisoft.artefactos.application.versionartefacto.query.validator;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;

public interface DescargarArchivoVersionArtefactoValidator {

    void validar(DescargaVersionArtefactoCriteria criteria, ArchivoVersionArtefactoDomain archivo, boolean participa);
}
