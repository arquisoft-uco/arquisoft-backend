package com.arquisoft.artefactos.application.versionartefacto.query.usecase;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface DescargarArchivoVersionArtefactoUseCase
        extends UseCase<DescargaVersionArtefactoCriteria, EnlaceDescargaVersionArtefactoReadModel> {}
