package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionRepresentanteFichaCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilValidatorImpl implements RegistrarEvaluacionFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final RepresentanteComiteExisteRule representanteComiteExisteRule;
    private final EvaluacionNoDuplicadaRule evaluacionNoDuplicadaRule;

    @Override
    public void validar(EvaluacionFichaPerfilAggregate evaluacion) {
        fichaPerfilExisteRule.validar(evaluacion.getFichaPerfilId());
        representanteComiteExisteRule.validar(evaluacion.getRepresentanteComiteId());
        evaluacionNoDuplicadaRule.validar(new EvaluacionRepresentanteFichaCriteria(
                evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId()));
    }
}
