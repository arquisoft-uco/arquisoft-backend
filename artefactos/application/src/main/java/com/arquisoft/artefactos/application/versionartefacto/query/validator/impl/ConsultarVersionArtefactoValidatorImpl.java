package com.arquisoft.artefactos.application.versionartefacto.query.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.ConsultarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.model.AccesoVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefactoConsultada;
import com.arquisoft.artefactos.domain.versionartefacto.rules.ActorAccedeVersionArtefactoRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoConsultadaExisteRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.ActorAccedeVersionArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.VersionArtefactoConsultadaExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarVersionArtefactoValidatorImpl implements ConsultarVersionArtefactoValidator {

    private final VersionArtefactoConsultadaExisteRule versionArtefactoConsultadaExisteRule;
    private final ActorAccedeVersionArtefactoRule actorAccedeVersionArtefactoRule;

    public ConsultarVersionArtefactoValidatorImpl() {
        this.versionArtefactoConsultadaExisteRule = new VersionArtefactoConsultadaExisteRuleImpl();
        this.actorAccedeVersionArtefactoRule = new ActorAccedeVersionArtefactoRuleImpl();
    }

    @Override
    public void validar(VersionArtefactoDetalleCriteria criteria, UUID asesor, boolean participa) {
        versionArtefactoConsultadaExisteRule.validar(
                new ExistenciaVersionArtefactoConsultada(criteria.versionArtefacto(), asesor));
        actorAccedeVersionArtefactoRule.validar(new AccesoVersionArtefacto(
                criteria.versionArtefacto(), criteria.actor(), criteria.comoAsesor(), criteria.comoEstudiante(),
                asesor, participa));
    }
}
