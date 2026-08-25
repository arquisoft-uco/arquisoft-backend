package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VinculoEstudianteFichaExisteFinderImpl implements VinculoEstudianteFichaExisteFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public Boolean obtener(VinculoEstudianteFicha vinculo) {
        return estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                vinculo.fichaPerfil(), vinculo.estudiante());
    }
}
