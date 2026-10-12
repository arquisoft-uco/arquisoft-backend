package com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesEnCursoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.model.IntegrantesVigentesFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.TransicionEstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilNoRepetidoRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEstudiantesVigentesRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilSinEvaluacionEnCursoRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.TransicionEstadoFichaPermitidaRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilNoRepetidoRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.FichaPerfilConEstudiantesVigentesRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.FichaPerfilSinEvaluacionEnCursoRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.TransicionEstadoFichaPermitidaRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgregarEstadoFichaPerfilValidatorImpl implements AgregarEstadoFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final EstadoFichaPerfilNoRepetidoRule estadoFichaPerfilNoRepetidoRule;
    private final TransicionEstadoFichaPermitidaRule transicionEstadoFichaPermitidaRule;
    private final FichaPerfilSinEvaluacionEnCursoRule fichaPerfilSinEvaluacionEnCursoRule;
    private final FichaPerfilConEstudiantesVigentesRule fichaPerfilConEstudiantesVigentesRule;

    public AgregarEstadoFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
        this.estadoFichaPerfilNoRepetidoRule = new EstadoFichaPerfilNoRepetidoRuleImpl();
        this.transicionEstadoFichaPermitidaRule = new TransicionEstadoFichaPermitidaRuleImpl();
        this.fichaPerfilSinEvaluacionEnCursoRule = new FichaPerfilSinEvaluacionEnCursoRuleImpl();
        this.fichaPerfilConEstudiantesVigentesRule = new FichaPerfilConEstudiantesVigentesRuleImpl();
    }

    @Override
    public void validar(AgregacionEstadoFichaPerfilDomain entrada, FichaPerfilDomain ficha,
                        EstadoFichaPerfilDomain estadoActual, ResumenEvaluacionesFicha resumen,
                        List<IntegranteFicha> integrantes) {
        var fichaPerfil = entrada.getFichaPerfil();
        var actual = estadoActual.getEstadoFicha();
        var transicion = new TransicionEstadoFicha(fichaPerfil, actual, entrada.getEstado().getEstadoFicha());

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, !ficha.esVacio()));
        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(fichaPerfil, ficha.getAsesorFicha(), entrada.getAsesorFicha()));
        estadoFichaPerfilExisteRule.validar(new ExistenciaEstadoFichaPerfil(fichaPerfil, !estadoActual.esVacio()));
        estadoFichaPerfilEnTerminalRule.validar(new EstadoActualFicha(fichaPerfil, actual));
        estadoFichaPerfilNoRepetidoRule.validar(transicion);
        transicionEstadoFichaPermitidaRule.validar(transicion);
        fichaPerfilSinEvaluacionEnCursoRule.validar(
                new EvaluacionesEnCursoFicha(fichaPerfil, actual, resumen.enEvaluacion()));
        fichaPerfilConEstudiantesVigentesRule.validar(new IntegrantesVigentesFicha(fichaPerfil, integrantes.size()));
    }
}
