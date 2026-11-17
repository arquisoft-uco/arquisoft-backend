package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.CambiarCriterioEvaluacionCualitativaJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.CriterioActualVsNuevo;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoCambioCriterioEvaluacionCualitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionCualitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.CriterioEvaluacionCualitativaJuradoDistintoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.CriteriosCualitativosJuradoExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionCualitativaJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoAdmiteCambioCriterioRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.CriterioEvaluacionCualitativaJuradoDistintoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.CriteriosCualitativosJuradoExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionCualitativaJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoAdmiteCambioCriterioRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class CambiarCriterioEvaluacionCualitativaJuradoValidatorImpl
        implements CambiarCriterioEvaluacionCualitativaJuradoValidator {

    private final EvaluacionCualitativaJuradoExistenteRule evaluacionExistenteRule;
    private final CriteriosCualitativosJuradoExistentesRule criteriosExistentesRule;
    private final EvaluacionJuradoPropiedadJuradoRule propiedadJuradoRule;
    private final EvaluacionJuradoAdmiteCambioCriterioRule admiteCambioCriterioRule;
    private final CriterioEvaluacionCualitativaJuradoDistintoRule criterioDistintoRule;

    public CambiarCriterioEvaluacionCualitativaJuradoValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionCualitativaJuradoExistenteRuleImpl();
        this.criteriosExistentesRule = new CriteriosCualitativosJuradoExistentesRuleImpl();
        this.propiedadJuradoRule = new EvaluacionJuradoPropiedadJuradoRuleImpl();
        this.admiteCambioCriterioRule = new EvaluacionJuradoAdmiteCambioCriterioRuleImpl();
        this.criterioDistintoRule = new CriterioEvaluacionCualitativaJuradoDistintoRuleImpl();
    }

    @Override
    public void validar(
            CambioCriterioEvaluacionCualitativaJuradoDomain cambio,
            EvaluacionCualitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoDomain estado,
            Set<UUID> criteriosExistentes) {
        var evaluacionCualitativaJurado = cambio.getEvaluacionCualitativaJurado();

        evaluacionExistenteRule.validar(
                new ExistenciaEvaluacionCualitativaJurado(evaluacionCualitativaJurado, !evaluacion.esVacio()));
        criteriosExistentesRule.validar(
                new ExistenciaCriteriosCualitativosJurado(Set.of(cambio.getCriterio()), criteriosExistentes));
        propiedadJuradoRule.validar(
                new PropiedadEvaluacionJurado(evaluacion.getEvaluacionJurado(), estado.getJurado(), cambio.getJurado()));
        admiteCambioCriterioRule.validar(
                new EstadoCambioCriterioEvaluacionCualitativaJurado(evaluacionCualitativaJurado, estado.getEstado()));
        criterioDistintoRule.validar(
                new CriterioActualVsNuevo(evaluacionCualitativaJurado, evaluacion.getCriterio(), cambio.getCriterio()));
    }
}
