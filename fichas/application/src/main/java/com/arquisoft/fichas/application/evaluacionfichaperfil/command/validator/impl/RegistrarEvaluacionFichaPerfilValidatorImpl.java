package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.DisponibilidadEvaluacionFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ExistenciaRepresentanteComite;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.EvaluacionNoDuplicadaRuleImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.RepresentanteComiteExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarEvaluacionFichaPerfilValidatorImpl implements RegistrarEvaluacionFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
    private final RepresentanteComiteExisteRule representanteComiteExisteRule = new RepresentanteComiteExisteRuleImpl();
    private final EvaluacionNoDuplicadaRule evaluacionNoDuplicadaRule = new EvaluacionNoDuplicadaRuleImpl();

    @Override
    public void validar(EvaluacionFichaPerfilDomain evaluacion, boolean fichaExiste, boolean representanteExiste,
                        boolean evaluacionYaExiste) {

        fichaPerfilExisteRule.validar(
                new ExistenciaFichaPerfil(evaluacion.getFichaPerfilId(), fichaExiste));

        representanteComiteExisteRule.validar(
                new ExistenciaRepresentanteComite(evaluacion.getRepresentanteComiteId(), representanteExiste));

        evaluacionNoDuplicadaRule.validar(new DisponibilidadEvaluacionFicha(
                evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId(), evaluacionYaExiste));
    }
}
