package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilFinderImpl implements FichaPerfilFinder {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public FichaPerfilDomain obtener(UUID fichaPerfilId) {
        return fichaPerfilOutputPort.buscarPorId(fichaPerfilId)
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(fichaPerfilId));
    }
}
