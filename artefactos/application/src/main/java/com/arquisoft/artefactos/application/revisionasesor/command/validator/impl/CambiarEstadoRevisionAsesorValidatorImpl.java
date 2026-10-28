package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.validator.CambiarEstadoRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesorDeRevision;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorDeRevisionExisteRule;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorDeRevisionExisteRuleImpl;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudianteExisteRule;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudianteExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.DestinoCambioEstado;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.domain.revisionasesor.rules.EstadoDestinoRevisionAsesorPermitidoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.EstudianteParticipaArtefactoRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorNoCerradaRule;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstadoDestinoRevisionAsesorPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstudianteParticipaArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoRevisionAsesorValidatorImpl implements CambiarEstadoRevisionAsesorValidator {

    private final RevisionAsesorExisteRule revisionAsesorExisteRule;
    private final EstudianteExisteRule estudianteExisteRule;
    private final AsesorDeRevisionExisteRule asesorDeRevisionExisteRule;
    private final EstudianteParticipaArtefactoRule estudianteParticipaArtefactoRule;
    private final RevisionAsesorNoCerradaRule revisionAsesorNoCerradaRule;
    private final EstadoDestinoRevisionAsesorPermitidoRule estadoDestinoRevisionAsesorPermitidoRule;

    public CambiarEstadoRevisionAsesorValidatorImpl() {
        this.revisionAsesorExisteRule = new RevisionAsesorExisteRuleImpl();
        this.estudianteExisteRule = new EstudianteExisteRuleImpl();
        this.asesorDeRevisionExisteRule = new AsesorDeRevisionExisteRuleImpl();
        this.estudianteParticipaArtefactoRule = new EstudianteParticipaArtefactoRuleImpl();
        this.revisionAsesorNoCerradaRule = new RevisionAsesorNoCerradaRuleImpl();
        this.estadoDestinoRevisionAsesorPermitidoRule = new EstadoDestinoRevisionAsesorPermitidoRuleImpl();
    }

    @Override
    public void validar(CambioEstadoRevisionAsesorDomain cambio, RevisionAsesorDomain revision, AsesorDomain asesor,
                        EstudianteDomain estudiante, boolean participa) {

        revisionAsesorExisteRule.validar(
                new ExistenciaRevisionAsesor(cambio.getRevisionAsesor(), !revision.esVacio()));
        estudianteExisteRule.validar(
                new ExistenciaEstudiante(cambio.getEstudiante(), !estudiante.esVacio()));
        asesorDeRevisionExisteRule.validar(
                new ExistenciaAsesorDeRevision(cambio.getRevisionAsesor(), !asesor.esVacio()));
        estudianteParticipaArtefactoRule.validar(
                new ParticipacionEstudianteArtefacto(cambio.getRevisionAsesor(), cambio.getEstudiante(), participa));

        revisionAsesorNoCerradaRule.validar(
                new EstadoActualRevisionAsesor(revision.getId(), revision.getEstado()));
        estadoDestinoRevisionAsesorPermitidoRule.validar(
                new DestinoCambioEstado(cambio.getRevisionAsesor(), cambio.getEstado()));
    }
}
