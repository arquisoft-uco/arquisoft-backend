package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper.FichaPerfilMapper;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilFinderImpl implements FichaPerfilFinder {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public Optional<FichaPerfilDomain> obtener(UUID fichaPerfil) {
        return fichaPerfilOutputPort.buscarPorId(fichaPerfil)
                .map(FichaPerfilMapper::toDomain);
    }
}
