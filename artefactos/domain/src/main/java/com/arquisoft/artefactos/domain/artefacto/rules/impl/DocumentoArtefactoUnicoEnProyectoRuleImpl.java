package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.DocumentoArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadDocumentoProyecto;
import com.arquisoft.artefactos.domain.artefacto.rules.DocumentoArtefactoUnicoEnProyectoRule;

public class DocumentoArtefactoUnicoEnProyectoRuleImpl implements DocumentoArtefactoUnicoEnProyectoRule {

    @Override
    public void validar(DisponibilidadDocumentoProyecto disponibilidad) {
        var version = disponibilidad.version();
        var yaAsignado = disponibilidad.versionesAsignadas().stream()
                .anyMatch(asignada -> asignada.getRepositorioArtefacto().equals(version.getRepositorioArtefacto())
                        && asignada.getNombreDocumento().equalsIgnoreCase(version.getNombreDocumento()));
        if (yaAsignado) {
            throw new DocumentoArtefactoYaAsignadoException(
                    version.getNombreDocumento(), version.getNombreRepositorio(), version.getRepositorioArtefacto());
        }
    }
}
