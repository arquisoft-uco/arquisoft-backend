package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;

import java.util.UUID;

public interface EnlaceDescargaVersionArtefactoQueryOutputPort {

    EnlaceDescargaVersionArtefactoReadModel generar(UUID versionArtefacto, String uriArchivo, String nombreRespaldo);
}
