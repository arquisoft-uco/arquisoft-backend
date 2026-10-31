package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.application.observacion.command.validator.RegistrarObservacionValidator;
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
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadObservacionRevision;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionDescripcionUnicaRule;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionDescripcionUnicaRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteObservacionesRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteObservacionesRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarObservacionValidatorImpl implements RegistrarObservacionValidator {

    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorAdmiteObservacionesRule revisionAsesorAdmiteObservacionesRule;
    private final ObservacionDescripcionUnicaRule observacionDescripcionUnicaRule;

    public RegistrarObservacionValidatorImpl() {
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorAdmiteObservacionesRule = new RevisionAsesorAdmiteObservacionesRuleImpl();
        this.observacionDescripcionUnicaRule = new ObservacionDescripcionUnicaRuleImpl();
    }

    @Override
    public void validar(RegistroObservacionDomain registro, RevisionAsesorDomain revision, ArtefactoDomain artefacto,
                        boolean descripcionExiste) {
        var revisionAsesor = registro.getObservacion().getRevisionAsesor();

        revisionAsesorExisteRule.validar(new ExistenciaRevisionAsesor(revisionAsesor, !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(registro.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorAdmiteObservacionesRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        observacionDescripcionUnicaRule.validar(new UnicidadObservacionRevision(revisionAsesor, descripcionExiste));
    }
}
