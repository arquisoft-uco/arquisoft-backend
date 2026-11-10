package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.projection;

import java.time.Instant;
import java.util.UUID;

public interface ArtefactoParaPaqueteProjection {

    UUID getArtefacto();

    String getNombreDocumento();

    UUID getVersionArtefacto();

    Integer getNumeroVersionCerrada();

    String getUriArchivo();

    Instant getFechaVersion();

    Integer getUltimaVersion();

    UUID getVersionRepositorioArtefacto();

    UUID getRepositorio();

    String getNombreRepositorio();
}
