package com.arquisoft.fichas.application.estadofichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoActualFichaPerfilFinderImpl implements EstadoActualFichaPerfilFinder {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Override
    public Optional<String> obtener(UUID fichaPerfil) {
        return estadoFichaPerfilOutputPort.obtenerEstadoActual(fichaPerfil)
                .map(EstadoFichaPerfilMapper::toDomain)
                .map(EstadoFichaPerfilDomain::getEstadoFicha)
                .map(EstadoFicha::getId);
    }
}
