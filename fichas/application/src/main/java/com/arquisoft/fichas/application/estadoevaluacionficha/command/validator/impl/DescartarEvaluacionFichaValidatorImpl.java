package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.DescartarEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionDescartableRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionDescartableRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilDisponibleParaEvaluacionRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DescartarEvaluacionFichaValidatorImpl implements DescartarEvaluacionFichaValidator {

    private final EvaluacionFichaExisteRule evaluacionFichaExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EvaluacionDescartableRule evaluacionDescartableRule;
    private final EstadoFichaPerfilDisponibleParaEvaluacionRule estadoDisponibleParaEvaluacionRule;

    public DescartarEvaluacionFichaValidatorImpl() {
        this.evaluacionFichaExisteRule = new EvaluacionFichaExisteRuleImpl();
        this.representantePropietarioEvaluacionRule = new RepresentantePropietarioEvaluacionRuleImpl();
        this.evaluacionDescartableRule = new EvaluacionDescartableRuleImpl();
        this.estadoDisponibleParaEvaluacionRule = new EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl();
    }

    @Override
    public void validar(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste,
                        boolean esPropietario, EstadoEvaluacionFichaDomain ultimoEstado,
                        EstadoActualFicha estadoFicha) {

        evaluacionFichaExisteRule.validar(
                new ExistenciaEvaluacionFicha(entrada.getEvaluacionFichaPerfil(), evaluacionExiste));

        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite(), esPropietario));

        evaluacionDescartableRule.validar(new UltimoEstadoEvaluacion(
                entrada.getEvaluacionFichaPerfil(), ultimoEstado.getEstadoEvaluacion()));

        estadoDisponibleParaEvaluacionRule.validar(estadoFicha);
    }
}
