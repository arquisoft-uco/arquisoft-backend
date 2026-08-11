package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.shared.util.UtilCollection;

import java.util.List;

public class EstudiantesNoVinculadosRuleImpl implements EstudiantesNoVinculadosRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public EstudiantesNoVinculadosRuleImpl(EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(List<EstudianteFichaPerfilDomain> relaciones) {
        if (UtilCollection.isEmptyOrNull(relaciones)) {
            return;
        }
        relaciones.forEach(relacion -> {
            if (estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                    relacion.getFichaPerfilId(), relacion.getEstudianteId())) {
                throw new EstudianteDuplicadoException(relacion.getEstudianteId());
            }
        });
    }
}
