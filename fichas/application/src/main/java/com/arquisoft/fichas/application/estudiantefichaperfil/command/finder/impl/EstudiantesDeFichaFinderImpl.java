package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesDeFichaFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesDeFichaFinderImpl implements EstudiantesDeFichaFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public List<UUID> obtener(UUID fichaPerfilId) {
        return estudianteFichaPerfilOutputPort.obtenerEstudiantesDeFicha(fichaPerfilId);
    }
}
