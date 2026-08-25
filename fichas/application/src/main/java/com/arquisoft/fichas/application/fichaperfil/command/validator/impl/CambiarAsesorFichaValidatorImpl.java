package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.AsesorFichaComparacion;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaDiferenteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarAsesorFichaValidatorImpl implements CambiarAsesorFichaValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    public CambiarAsesorFichaValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.asesorFichaExisteRule = new AsesorFichaExisteRuleImpl();
        this.estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
        this.asesorFichaDiferenteRule = new AsesorFichaDiferenteRuleImpl();
    }

    @Override
    public void validar(CambioAsesorFichaDomain cambio, FichaPerfilDomain ficha, AsesorFichaDomain asesorFicha,
                        EstadoFichaPerfilDomain estadoActual) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(cambio.getFichaPerfil(), !ficha.esVacio()));
        asesorFichaExisteRule.validar(
                new ExistenciaAsesorFicha(cambio.getNuevoAsesorFicha(), !asesorFicha.esVacio()));

        estadoFichaPerfilExisteRule.validar(
                new ExistenciaEstadoFichaPerfil(ficha.getId(), !estadoActual.esVacio()));
        estadoFichaPerfilEnTerminalRule.validar(
                new EstadoActualFicha(ficha.getId(), estadoActual.getEstadoFicha()));

        asesorFichaDiferenteRule.validar(
                new AsesorFichaComparacion(cambio.getNuevoAsesorFicha(), ficha.getAsesorFicha()));
    }
}
