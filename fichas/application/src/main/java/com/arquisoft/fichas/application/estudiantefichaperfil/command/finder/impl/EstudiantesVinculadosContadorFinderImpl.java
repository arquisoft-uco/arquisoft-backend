package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesVinculadosContadorFinderImpl implements EstudiantesVinculadosContadorFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public Long obtener(UUID fichaPerfil) {
        return estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfil);
    }
}
