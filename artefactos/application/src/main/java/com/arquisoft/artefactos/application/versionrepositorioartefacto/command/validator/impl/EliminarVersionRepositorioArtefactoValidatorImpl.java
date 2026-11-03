package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.EliminarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.UsoVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.VigenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoSinUsoRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoUltimaParaEliminarRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoSinUsoRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoUltimaParaEliminarRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EliminarVersionRepositorioArtefactoValidatorImpl
        implements EliminarVersionRepositorioArtefactoValidator {

    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final VersionRepositorioArtefactoUltimaParaEliminarRule versionRepositorioArtefactoUltimaParaEliminarRule;
    private final VersionRepositorioArtefactoSinUsoRule versionRepositorioArtefactoSinUsoRule;

    public EliminarVersionRepositorioArtefactoValidatorImpl() {
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.versionRepositorioArtefactoUltimaParaEliminarRule = new VersionRepositorioArtefactoUltimaParaEliminarRuleImpl();
        this.versionRepositorioArtefactoSinUsoRule = new VersionRepositorioArtefactoSinUsoRuleImpl();
    }

    @Override
    public void validar(
            EliminacionVersionRepositorioArtefactoDomain eliminacion,
            VersionRepositorioArtefactoDomain version,
            int ultimaVersionDocumento,
            boolean enUso) {
        versionRepositorioArtefactoExisteRule.validar(
                new ExistenciaVersionRepositorioArtefacto(eliminacion.getVersion(), version));
        versionRepositorioArtefactoUltimaParaEliminarRule.validar(
                new VigenciaVersionRepositorioArtefacto(version, ultimaVersionDocumento));
        versionRepositorioArtefactoSinUsoRule.validar(new UsoVersionRepositorioArtefacto(version, enUso));
    }
}
