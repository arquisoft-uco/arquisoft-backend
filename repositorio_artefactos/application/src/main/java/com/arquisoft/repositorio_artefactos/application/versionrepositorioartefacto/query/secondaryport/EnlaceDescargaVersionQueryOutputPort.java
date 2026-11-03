package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;

import java.util.UUID;

public interface EnlaceDescargaVersionQueryOutputPort {

    EnlaceDescargaVersionReadModel generar(UUID version, String uriArchivo, String nombreArchivo);
}
