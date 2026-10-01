package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilExisteFinderImpl implements FichaPerfilExisteFinder {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public Boolean obtener(UUID fichaPerfil) {
        return fichaPerfilOutputPort.existePorId(fichaPerfil);
    }
}
