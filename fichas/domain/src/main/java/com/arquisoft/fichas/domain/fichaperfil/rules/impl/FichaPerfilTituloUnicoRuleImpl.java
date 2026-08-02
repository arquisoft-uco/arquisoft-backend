package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;

public class FichaPerfilTituloUnicoRuleImpl implements FichaPerfilTituloUnicoRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public FichaPerfilTituloUnicoRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(String tituloProyecto) {
        if (fichaPerfilOutputPort.existePorTituloProyecto(tituloProyecto)) {
            throw new FichaTituloDuplicadoException(tituloProyecto);
        }
    }
}
