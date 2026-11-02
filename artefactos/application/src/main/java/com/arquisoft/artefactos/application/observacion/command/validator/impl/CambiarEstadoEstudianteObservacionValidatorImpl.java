package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoEstudianteObservacionValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.ArtefactoNoOmitidoRuleImpl;
import com.arquisoft.artefactos.domain.artefacto.rules.impl.EstudiantePropietarioArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.ExistenciaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.EstadoDestinoObservacionEstudiantePermitidoRule;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionEditablePorEstudianteRule;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionExisteRule;
import com.arquisoft.artefactos.domain.observacion.rules.impl.EstadoDestinoObservacionEstudiantePermitidoRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionEditablePorEstudianteRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteAvanceObservacionRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteAvanceObservacionRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoEstudianteObservacionValidatorImpl implements CambiarEstadoEstudianteObservacionValidator {

    private final ObservacionExisteRule observacionExisteRule;
    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final EstudiantePropietarioArtefactoRule estudiantePropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorAdmiteAvanceObservacionRule revisionAsesorAdmiteAvanceObservacionRule;
    private final ObservacionEditablePorEstudianteRule observacionEditablePorEstudianteRule;
    private final EstadoDestinoObservacionEstudiantePermitidoRule estadoDestinoObservacionEstudiantePermitidoRule;

    public CambiarEstadoEstudianteObservacionValidatorImpl() {
        this.observacionExisteRule = new ObservacionExisteRuleImpl();
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.estudiantePropietarioArtefactoRule = new EstudiantePropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorAdmiteAvanceObservacionRule = new RevisionAsesorAdmiteAvanceObservacionRuleImpl();
        this.observacionEditablePorEstudianteRule = new ObservacionEditablePorEstudianteRuleImpl();
        this.estadoDestinoObservacionEstudiantePermitidoRule =
                new EstadoDestinoObservacionEstudiantePermitidoRuleImpl();
    }

    @Override
    public void validar(CambioEstadoEstudianteObservacionDomain cambio, ObservacionDomain observacion,
                        RevisionAsesorDomain revision, ArtefactoDomain artefacto, boolean vinculado) {

        observacionExisteRule.validar(new ExistenciaObservacion(cambio.getObservacion(), !observacion.esVacio()));
        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(observacion.getRevisionAsesor(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));

        estudiantePropietarioArtefactoRule.validar(
                new PropiedadArtefacto(cambio.getEstudiante(), artefacto.getId(), vinculado));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorAdmiteAvanceObservacionRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        observacionEditablePorEstudianteRule.validar(
                new EstadoActualObservacion(observacion.getId(), observacion.getEstado()));
        estadoDestinoObservacionEstudiantePermitidoRule.validar(
                new DestinoCambioEstadoObservacion(cambio.getObservacion(), cambio.getEstado()));
    }
}
