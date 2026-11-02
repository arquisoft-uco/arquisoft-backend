package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.validator.EnviarRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.AsesorPropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.EnvioRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorConObservacionesRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorEnviableRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorConObservacionesRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorEnviableRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarRevisionAsesorValidatorImpl implements EnviarRevisionAsesorValidator {

    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionArtefactoExisteRule versionArtefactoExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorEnviableRule revisionAsesorEnviableRule;
    private final RevisionAsesorConObservacionesRule revisionAsesorConObservacionesRule;

    public EnviarRevisionAsesorValidatorImpl() {
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionArtefactoExisteRule = new VersionArtefactoExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorEnviableRule = new RevisionAsesorEnviableRuleImpl();
        this.revisionAsesorConObservacionesRule = new RevisionAsesorConObservacionesRuleImpl();
    }

    @Override
    public void validar(EnvioRevisionAsesorDomain envio, RevisionAsesorDomain revision, ArtefactoDomain artefacto,
                        AsesorDomain asesor, VersionArtefactoDomain version,
                        VersionRepositorioArtefactoDomain versionRepositorio,
                        ObservacionesDeRevision observaciones) {

        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(envio.getRevisionAsesor(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(envio.getAsesor(), asesor));
        versionArtefactoExisteRule.validar(
                new ExistenciaVersionArtefacto(revision.getVersionArtefacto(), version));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));

        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(envio.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorEnviableRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        revisionAsesorConObservacionesRule.validar(observaciones);
    }
}
