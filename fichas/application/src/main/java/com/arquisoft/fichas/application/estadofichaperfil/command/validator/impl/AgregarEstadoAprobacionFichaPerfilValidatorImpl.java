package com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoAprobacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.IntegrantesVigentesFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.RespaldoAprobacionFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.AprobacionRespaldadaPorEvaluacionRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilDisponibleParaEvaluacionRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEstudiantesVigentesRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEvaluacionFinalizadaRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.AprobacionRespaldadaPorEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.FichaPerfilConEstudiantesVigentesRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.FichaPerfilConEvaluacionFinalizadaRuleImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgregarEstadoAprobacionFichaPerfilValidatorImpl implements AgregarEstadoAprobacionFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstadoFichaPerfilDisponibleParaEvaluacionRule estadoDisponibleParaEvaluacionRule;
    private final FichaPerfilConEvaluacionFinalizadaRule evaluacionFinalizadaRule;
    private final AprobacionRespaldadaPorEvaluacionRule aprobacionRespaldadaRule;
    private final FichaPerfilConEstudiantesVigentesRule estudiantesVigentesRule;

    public AgregarEstadoAprobacionFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.asesorFichaExisteRule = new AsesorFichaExisteRuleImpl();
        this.estadoDisponibleParaEvaluacionRule = new EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl();
        this.evaluacionFinalizadaRule = new FichaPerfilConEvaluacionFinalizadaRuleImpl();
        this.aprobacionRespaldadaRule = new AprobacionRespaldadaPorEvaluacionRuleImpl();
        this.estudiantesVigentesRule = new FichaPerfilConEstudiantesVigentesRuleImpl();
    }

    @Override
    public void validar(DecisionFichaPerfilDomain decision, FichaPerfilDomain ficha, AsesorFichaDomain asesor,
                        EstadoFichaPerfilDomain estadoActual, ResumenEvaluacionesFicha resumen,
                        List<IntegranteFicha> integrantes) {
        var fichaPerfil = decision.getFichaPerfil();

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, !ficha.esVacio()));
        asesorFichaExisteRule.validar(new ExistenciaAsesorFicha(ficha.getAsesorFicha(), !asesor.esVacio()));
        estadoDisponibleParaEvaluacionRule.validar(new EstadoActualFicha(fichaPerfil, estadoActual.getEstadoFicha()));
        evaluacionFinalizadaRule.validar(resumen);
        aprobacionRespaldadaRule.validar(new RespaldoAprobacionFicha(decision.isAcepta(), resumen));
        estudiantesVigentesRule.validar(new IntegrantesVigentesFicha(fichaPerfil, integrantes.size()));
    }
}
