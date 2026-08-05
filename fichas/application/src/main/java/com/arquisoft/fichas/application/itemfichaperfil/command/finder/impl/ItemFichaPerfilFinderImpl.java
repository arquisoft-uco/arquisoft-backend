package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilFinderImpl implements ItemFichaPerfilFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public ItemFichaPerfilDomain obtener(UUID itemId) {
        return itemFichaPerfilOutputPort.buscarPorId(itemId)
                .orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(itemId));
    }
}
