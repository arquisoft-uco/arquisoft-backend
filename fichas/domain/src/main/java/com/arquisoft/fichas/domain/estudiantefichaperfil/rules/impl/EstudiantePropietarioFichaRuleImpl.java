package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropietarioFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;

public class EstudiantePropietarioFichaRuleImpl implements EstudiantePropietarioFichaRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public EstudiantePropietarioFichaRuleImpl(EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(PropietarioFicha propietario) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                propietario.fichaPerfil(), propietario.estudiante())) {
            throw new FichaNoPropietarioException(propietario.fichaPerfil(), propietario.estudiante());
        }
    }
}
