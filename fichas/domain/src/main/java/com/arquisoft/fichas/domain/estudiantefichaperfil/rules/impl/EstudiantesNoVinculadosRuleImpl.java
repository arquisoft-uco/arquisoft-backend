package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudiantesCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.shared.util.UtilCollection;

public class EstudiantesNoVinculadosRuleImpl implements EstudiantesNoVinculadosRule {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public EstudiantesNoVinculadosRuleImpl(EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        this.estudianteFichaPerfilOutputPort = estudianteFichaPerfilOutputPort;
    }

    @Override
    public void validar(VinculacionEstudiantesCriteria criteria) {
        if (UtilCollection.isEmptyOrNull(criteria.estudiantes())) {
            return;
        }
        criteria.estudiantes().forEach(estudiante -> {
            if (estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(criteria.fichaPerfil(), estudiante)) {
                throw new EstudianteDuplicadoException(estudiante);
            }
        });
    }
}
