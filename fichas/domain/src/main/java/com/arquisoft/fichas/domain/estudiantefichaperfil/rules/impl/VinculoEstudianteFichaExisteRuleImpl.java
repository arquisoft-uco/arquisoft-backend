package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;

public class VinculoEstudianteFichaExisteRuleImpl implements VinculoEstudianteFichaExisteRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public VinculoEstudianteFichaExisteRuleImpl(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(RemocionEstudianteFichaPerfilDomain entrada) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                entrada.getFichaPerfil(), entrada.getEstudiante())) {
            throw new EstudianteFichaPerfilNoEncontradoException(
                    entrada.getEstudiante(), entrada.getFichaPerfil());
        }
    }
}
