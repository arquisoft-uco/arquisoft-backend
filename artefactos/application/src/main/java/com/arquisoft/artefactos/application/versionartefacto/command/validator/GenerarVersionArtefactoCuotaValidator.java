package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;

public interface GenerarVersionArtefactoCuotaValidator {

    void validar(ArtefactoDomain artefacto, CargaPendienteDomain carga, AlmacenamientoProyectoGrado almacenamiento);
}
