package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.model.TituloFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;

public class FichaPerfilTituloDisponibleRuleImpl implements FichaPerfilTituloDisponibleRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public FichaPerfilTituloDisponibleRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(TituloFichaCriteria criteria) {
        if (criteria.tituloActual().equals(criteria.nuevoTitulo())) {
            return;
        }
        if (fichaPerfilOutputPort.existePorTituloProyecto(criteria.nuevoTitulo())) {
            throw new FichaTituloDuplicadoException(criteria.nuevoTitulo());
        }
    }
}
