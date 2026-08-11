package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;

public class FichaPerfilTituloDisponibleRuleImpl implements FichaPerfilTituloDisponibleRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public FichaPerfilTituloDisponibleRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(ModificacionFichaPerfilDomain modificacion) {
        if (fichaPerfilOutputPort.existeTituloEnOtraFicha(
                modificacion.getFichaPerfil(), modificacion.getTituloProyecto())) {
            throw new FichaTituloDuplicadoException(modificacion.getTituloProyecto());
        }
    }
}
