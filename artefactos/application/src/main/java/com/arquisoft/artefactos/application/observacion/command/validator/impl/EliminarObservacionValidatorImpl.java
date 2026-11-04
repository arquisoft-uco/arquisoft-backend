package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.application.observacion.command.validator.EliminarObservacionValidator;
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
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.ExistenciaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionExisteRule;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteEliminarObservacionesRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteEliminarObservacionesRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EliminarObservacionValidatorImpl implements EliminarObservacionValidator {

    private final ObservacionExisteRule observacionExisteRule;
    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorAdmiteEliminarObservacionesRule revisionAsesorAdmiteEliminarObservacionesRule;

    public EliminarObservacionValidatorImpl() {
        this.observacionExisteRule = new ObservacionExisteRuleImpl();
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorAdmiteEliminarObservacionesRule = new RevisionAsesorAdmiteEliminarObservacionesRuleImpl();
    }

    @Override
    public void validar(EliminacionObservacionDomain eliminacion, ObservacionDomain observacion,
                        RevisionAsesorDomain revision, ArtefactoDomain artefacto) {
        observacionExisteRule.validar(new ExistenciaObservacion(eliminacion.getObservacion(), !observacion.esVacio()));
        revisionAsesorExisteRule.validar(new ExistenciaRevisionAsesor(revision.getId(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(eliminacion.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorAdmiteEliminarObservacionesRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
    }
}
