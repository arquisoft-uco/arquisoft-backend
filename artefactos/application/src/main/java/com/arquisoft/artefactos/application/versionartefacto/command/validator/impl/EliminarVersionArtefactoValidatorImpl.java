package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.EliminarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionartefacto.rules.RevisionAnteriorPermiteEliminarVersionRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.RevisionAnteriorPermiteEliminarVersionRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesEliminacionVersion;
import com.arquisoft.artefactos.domain.versionartefacto.model.RevisionesVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoSinRevisionRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoUltimaParaEliminarRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoSinRevisionRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoUltimaParaEliminarRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EliminarVersionArtefactoValidatorImpl implements EliminarVersionArtefactoValidator {

    private final VersionArtefactoExisteRule versionArtefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final VersionArtefactoUltimaParaEliminarRule versionArtefactoUltimaParaEliminarRule;
    private final VersionArtefactoSinRevisionRule versionArtefactoSinRevisionRule;
    private final RevisionAnteriorPermiteEliminarVersionRule revisionAnteriorPermiteEliminarVersionRule;

    public EliminarVersionArtefactoValidatorImpl() {
        this.versionArtefactoExisteRule = new VersionArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.versionArtefactoUltimaParaEliminarRule = new VersionArtefactoUltimaParaEliminarRuleImpl();
        this.versionArtefactoSinRevisionRule = new VersionArtefactoSinRevisionRuleImpl();
        this.revisionAnteriorPermiteEliminarVersionRule = new RevisionAnteriorPermiteEliminarVersionRuleImpl();
    }

    @Override
    public void validar(EliminacionVersionArtefactoDomain eliminacion, PrecondicionesEliminacionVersion previas) {
        var version = previas.version();
        var versionArtefacto = eliminacion.getVersionArtefacto();

        versionArtefactoExisteRule.validar(new ExistenciaVersionArtefacto(versionArtefacto, version));
        estudiantePropietarioArtefactoRule.validar(
                new PropiedadArtefacto(eliminacion.getSolicitante(), version.getArtefacto(), previas.participa()));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(previas.artefacto()));
        versionArtefactoUltimaParaEliminarRule.validar(
                new VigenciaVersionArtefacto(versionArtefacto, version.getNumeroVersion(), previas.esUltima()));
        versionArtefactoSinRevisionRule.validar(
                new RevisionesVersionArtefacto(versionArtefacto, version.getNumeroVersion(), previas.tieneRevision()));
        revisionAnteriorPermiteEliminarVersionRule.validar(
                new RevisionVersionAnterior(version.getNumeroVersion() - 1, previas.revisionAnterior()));
    }
}
