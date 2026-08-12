package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TituloFichaPerfilExisteFinderImpl implements TituloFichaPerfilExisteFinder {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public Boolean obtener(String tituloProyecto) {
        return fichaPerfilOutputPort.existePorTituloProyecto(tituloProyecto);
    }
}
