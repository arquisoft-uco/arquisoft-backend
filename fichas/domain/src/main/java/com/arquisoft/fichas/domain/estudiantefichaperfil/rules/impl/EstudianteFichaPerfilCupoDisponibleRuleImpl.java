package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;

import java.util.List;

public class EstudianteFichaPerfilCupoDisponibleRuleImpl implements EstudianteFichaPerfilCupoDisponibleRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public EstudianteFichaPerfilCupoDisponibleRuleImpl(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(List<EstudianteFichaPerfilDomain> nuevasRelaciones) {
        long existentes = estudianteFichaPerfilOutputPort
                .contarPorFichaPerfilId(nuevasRelaciones.getFirst().getFichaPerfilId());

        if (existentes + nuevasRelaciones.size() > FichasLimits.FichaPerfil.ESTUDIANTES_MAX) {
            throw new CupoEstudiantesExcedidoException(FichasLimits.FichaPerfil.ESTUDIANTES_MAX);
        }
    }
}
