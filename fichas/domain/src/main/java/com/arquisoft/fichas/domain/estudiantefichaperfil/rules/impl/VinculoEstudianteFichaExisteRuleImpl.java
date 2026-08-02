package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudianteCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;

public class VinculoEstudianteFichaExisteRuleImpl implements VinculoEstudianteFichaExisteRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public VinculoEstudianteFichaExisteRuleImpl(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(VinculacionEstudianteCriteria criteria) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                criteria.fichaPerfil(), criteria.estudiante())) {
            throw new EstudianteFichaPerfilNoEncontradoException(
                    criteria.estudiante(), criteria.fichaPerfil());
        }
    }
}
