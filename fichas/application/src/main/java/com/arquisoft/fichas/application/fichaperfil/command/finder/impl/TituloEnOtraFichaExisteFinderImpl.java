package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloEnOtraFichaExisteFinder;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TituloEnOtraFichaExisteFinderImpl implements TituloEnOtraFichaExisteFinder {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public Boolean obtener(ModificacionFichaPerfilDomain modificacion) {
        return fichaPerfilOutputPort.existeTituloEnOtraFicha(
                modificacion.getFichaPerfil(), modificacion.getTituloProyecto());
    }
}
