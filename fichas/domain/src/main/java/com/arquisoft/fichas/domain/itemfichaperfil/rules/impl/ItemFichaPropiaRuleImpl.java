package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropietarioFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;

public class ItemFichaPropiaRuleImpl implements ItemFichaPropiaRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public ItemFichaPropiaRuleImpl(EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(PropietarioFicha propietario) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                propietario.fichaPerfil(), propietario.estudiante())) {
            throw new ItemFichaNoPropiaException(propietario.fichaPerfil());
        }
    }
}
