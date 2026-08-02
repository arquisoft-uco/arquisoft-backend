package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;

public class EstudiantePropietarioFichaRuleImpl implements EstudiantePropietarioFichaRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public EstudiantePropietarioFichaRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(PropietarioFichaCriteria criteria) {
        if (!fichaPerfilOutputPort.esEstudiantePropietario(criteria)) {
            throw new FichaNoPropietarioException(criteria.fichaPerfil(), criteria.estudiante());
        }
    }
}
