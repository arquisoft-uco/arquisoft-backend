package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.validator.EliminarRevisionAsesorValidator;
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
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorEliminableRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorEliminableRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EliminarRevisionAsesorValidatorImpl implements EliminarRevisionAsesorValidator {

    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorEliminableRule revisionAsesorEliminableRule;

    public EliminarRevisionAsesorValidatorImpl() {
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorEliminableRule = new RevisionAsesorEliminableRuleImpl();
    }

    @Override
    public void validar(EliminacionRevisionAsesorDomain eliminacion, RevisionAsesorDomain revision,
                        ArtefactoDomain artefacto) {
        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(eliminacion.getRevisionAsesor(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(eliminacion.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorEliminableRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
    }
}
