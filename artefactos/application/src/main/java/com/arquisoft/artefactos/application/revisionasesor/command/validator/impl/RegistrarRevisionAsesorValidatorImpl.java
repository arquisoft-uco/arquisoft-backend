package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.validator.RegistrarRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.AsesorPropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.UnicidadRevisionAbiertaVersion;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorUnicaAbiertaPorVersionRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorUnicaAbiertaPorVersionRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoEsUltimaRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoEsUltimaRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarRevisionAsesorValidatorImpl implements RegistrarRevisionAsesorValidator {

    private final VersionArtefactoExisteRule versionArtefactoExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final VersionArtefactoEsUltimaRule versionArtefactoEsUltimaRule;
    private final RevisionAsesorUnicaAbiertaPorVersionRule revisionAsesorUnicaAbiertaPorVersionRule;

    public RegistrarRevisionAsesorValidatorImpl() {
        this.versionArtefactoExisteRule = new VersionArtefactoExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.versionArtefactoEsUltimaRule = new VersionArtefactoEsUltimaRuleImpl();
        this.revisionAsesorUnicaAbiertaPorVersionRule = new RevisionAsesorUnicaAbiertaPorVersionRuleImpl();
    }

    @Override
    public void validar(RegistroRevisionAsesorDomain registro, VersionArtefactoDomain version, ArtefactoDomain artefacto,
                        boolean esUltima, boolean existeAbierta) {
        var versionArtefacto = registro.getRevision().getVersionArtefacto();

        versionArtefactoExisteRule.validar(new ExistenciaVersionArtefacto(versionArtefacto, version));
        artefactoExisteRule.validar(new ExistenciaArtefacto(version.getArtefacto(), artefacto));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(registro.getAsesor(), artefacto));
        versionArtefactoEsUltimaRule.validar(
                new VigenciaVersionArtefacto(versionArtefacto, version.getNumeroVersion(), esUltima));
        revisionAsesorUnicaAbiertaPorVersionRule.validar(
                new UnicidadRevisionAbiertaVersion(versionArtefacto, version.getNumeroVersion(), existeAbierta));
    }
}
