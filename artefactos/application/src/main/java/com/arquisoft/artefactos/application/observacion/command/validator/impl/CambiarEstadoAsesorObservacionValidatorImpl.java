package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoAsesorObservacionValidator;
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
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.ExistenciaObservacion;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadContinuacionObservacion;
import com.arquisoft.artefactos.domain.observacion.model.VersionesDeObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ContinuacionObservacionUnicaRule;
import com.arquisoft.artefactos.domain.observacion.rules.EstadoDestinoObservacionAsesorPermitidoRule;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionExisteRule;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionReabribleEnVersionRule;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionSolucionadaRule;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguientePendienteRule;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguienteRegistradaRule;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ContinuacionObservacionUnicaRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.EstadoDestinoObservacionAsesorPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionExisteRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionReabribleEnVersionRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionSolucionadaRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.RevisionVersionSiguientePendienteRuleImpl;
import com.arquisoft.artefactos.domain.observacion.rules.impl.RevisionVersionSiguienteRegistradaRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteDecisionObservacionRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteDecisionObservacionRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl.VersionRepositorioArtefactoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoAsesorObservacionValidatorImpl implements CambiarEstadoAsesorObservacionValidator {

    private final ObservacionExisteRule observacionExisteRule;
    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final ArtefactoExisteRule artefactoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final VersionArtefactoExisteRule versionArtefactoExisteRule;
    private final VersionRepositorioArtefactoExisteRule versionRepositorioArtefactoExisteRule;
    private final AsesorPropietarioArtefactoRule asesorPropietarioArtefactoRule;
    private final ArtefactoNoOmitidoRule artefactoNoOmitidoRule;
    private final RevisionAsesorAdmiteDecisionObservacionRule revisionAsesorAdmiteDecisionObservacionRule;
    private final ObservacionSolucionadaRule observacionSolucionadaRule;
    private final EstadoDestinoObservacionAsesorPermitidoRule estadoDestinoObservacionAsesorPermitidoRule;
    private final ObservacionReabribleEnVersionRule observacionReabribleEnVersionRule;
    private final RevisionVersionSiguienteRegistradaRule revisionVersionSiguienteRegistradaRule;
    private final RevisionVersionSiguientePendienteRule revisionVersionSiguientePendienteRule;
    private final ContinuacionObservacionUnicaRule continuacionObservacionUnicaRule;

    public CambiarEstadoAsesorObservacionValidatorImpl() {
        this.observacionExisteRule = new ObservacionExisteRuleImpl();
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.artefactoExisteRule = new ArtefactoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.versionArtefactoExisteRule = new VersionArtefactoExisteRuleImpl();
        this.versionRepositorioArtefactoExisteRule = new VersionRepositorioArtefactoExisteRuleImpl();
        this.asesorPropietarioArtefactoRule = new AsesorPropietarioArtefactoRuleImpl();
        this.artefactoNoOmitidoRule = new ArtefactoNoOmitidoRuleImpl();
        this.revisionAsesorAdmiteDecisionObservacionRule = new RevisionAsesorAdmiteDecisionObservacionRuleImpl();
        this.observacionSolucionadaRule = new ObservacionSolucionadaRuleImpl();
        this.estadoDestinoObservacionAsesorPermitidoRule = new EstadoDestinoObservacionAsesorPermitidoRuleImpl();
        this.observacionReabribleEnVersionRule = new ObservacionReabribleEnVersionRuleImpl();
        this.revisionVersionSiguienteRegistradaRule = new RevisionVersionSiguienteRegistradaRuleImpl();
        this.revisionVersionSiguientePendienteRule = new RevisionVersionSiguientePendienteRuleImpl();
        this.continuacionObservacionUnicaRule = new ContinuacionObservacionUnicaRuleImpl();
    }

    @Override
    public void validar(CambioEstadoAsesorObservacionDomain cambio, ObservacionDomain observacion,
                        RevisionAsesorDomain revision, ArtefactoDomain artefacto, AsesorDomain asesor,
                        VersionRepositorioArtefactoDomain versionRepositorio, VersionesDeObservacion versiones) {

        observacionExisteRule.validar(new ExistenciaObservacion(cambio.getObservacion(), !observacion.esVacio()));
        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(observacion.getRevisionAsesor(), !revision.esVacio()));
        artefactoExisteRule.validar(new ExistenciaArtefacto(artefacto.getId(), artefacto));
        asesorExisteRule.validar(new ExistenciaAsesor(cambio.getAsesor(), asesor));
        versionArtefactoExisteRule.validar(
                new ExistenciaVersionArtefacto(revision.getVersionArtefacto(), versiones.version()));
        versionRepositorioArtefactoExisteRule.validar(new ExistenciaVersionRepositorioArtefacto(
                artefacto.getVersionRepositorioArtefacto(), versionRepositorio));

        asesorPropietarioArtefactoRule.validar(new PropiedadArtefactoAsesor(cambio.getAsesor(), artefacto));
        artefactoNoOmitidoRule.validar(new ActividadArtefacto(artefacto));
        revisionAsesorAdmiteDecisionObservacionRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        observacionSolucionadaRule.validar(
                new EstadoActualObservacion(observacion.getId(), observacion.getEstado()));
        estadoDestinoObservacionAsesorPermitidoRule.validar(
                new DestinoCambioEstadoObservacion(cambio.getObservacion(), cambio.getEstado()));

        var situacion = new SituacionReaperturaObservacion(
                cambio.getObservacion(), cambio.getEstado(),
                versiones.version().getNumeroVersion(), versiones.numeroUltimaVersion(),
                versiones.revisionVersionSiguiente());
        observacionReabribleEnVersionRule.validar(situacion);
        revisionVersionSiguienteRegistradaRule.validar(situacion);
        revisionVersionSiguientePendienteRule.validar(situacion);
        continuacionObservacionUnicaRule.validar(new UnicidadContinuacionObservacion(
                cambio.getEstado(), versiones.revisionVersionSiguiente().getId(), versiones.descripcionRepetida()));
    }
}
