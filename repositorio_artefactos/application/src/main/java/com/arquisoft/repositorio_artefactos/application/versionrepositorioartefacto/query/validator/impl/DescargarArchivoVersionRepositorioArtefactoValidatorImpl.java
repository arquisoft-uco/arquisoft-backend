package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.validator.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.validator.DescargarArchivoVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExistenteRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExistenteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DescargarArchivoVersionRepositorioArtefactoValidatorImpl
        implements DescargarArchivoVersionRepositorioArtefactoValidator {

    private final VersionRepositorioArtefactoExistenteRule versionRepositorioArtefactoExistenteRule;

    public DescargarArchivoVersionRepositorioArtefactoValidatorImpl() {
        this.versionRepositorioArtefactoExistenteRule = new VersionRepositorioArtefactoExistenteRuleImpl();
    }

    @Override
    public void validar(ExistenciaVersionRepositorioArtefacto existencia) {
        versionRepositorioArtefactoExistenteRule.validar(existencia);
    }
}
