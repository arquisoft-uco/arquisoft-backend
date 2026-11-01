package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.validator.CerrarRevisionAsesorValidator;
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
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesParaCierre;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorCerrableRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.ObservacionesVersionAnteriorCerradasRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorSinObservacionesRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorCerrableRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.ObservacionesVersionAnteriorCerradasRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorSinObservacionesRuleImpl;
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
public class CerrarRevisionAsesorValidatorImpl implements CerrarRevisionAsesorValidator {

    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionArtefactoExisteRule versionArtefactoExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorCerrableRule revisionAsesorCerrableRule;
    private final RevisionAsesorSinObservacionesRule revisionAsesorSinObservacionesRule;
    private final ObservacionesVersionAnteriorCerradasRule observacionesVersionAnteriorCerradasRule;

    public CerrarRevisionAsesorValidatorImpl() {
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionArtefactoExisteRule = new VersionArtefactoExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorCerrableRule = new RevisionAsesorCerrableRuleImpl();
        this.revisionAsesorSinObservacionesRule = new RevisionAsesorSinObservacionesRuleImpl();
        this.observacionesVersionAnteriorCerradasRule = new ObservacionesVersionAnteriorCerradasRuleImpl();
    }

    @Override
    public void validar(CierreRevisionAsesorDomain cierre, RevisionAsesorDomain revision, ArtefactoDomain artefacto,
                        AsesorDomain asesor, VersionArtefactoDomain version,
                        VersionRepositorioArtefactoDomain versionRepositorio, ObservacionesParaCierre observaciones) {

        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(cierre.getRevisionAsesor(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(cierre.getAsesor(), asesor));
        versionArtefactoExisteRule.validar(
                new ExistenciaVersionArtefacto(revision.getVersionArtefacto(), version));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));

        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(cierre.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorCerrableRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        revisionAsesorSinObservacionesRule.validar(
                new ObservacionesDeRevision(revision.getId(), observaciones.existenPropias()));
        observacionesVersionAnteriorCerradasRule.validar(new ObservacionesVersionAnterior(
                revision.getId(), observaciones.existenSinCerrarVersionAnterior()));
    }
}
