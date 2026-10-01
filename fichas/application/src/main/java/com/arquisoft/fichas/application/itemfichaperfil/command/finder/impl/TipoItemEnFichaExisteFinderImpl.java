package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.TipoItemEnFichaExisteFinder;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoItemEnFichaExisteFinderImpl implements TipoItemEnFichaExisteFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public Boolean obtener(ItemFichaPerfilDomain item) {
        return itemFichaPerfilOutputPort.existePorFichaYTipoItem(
                item.getFichaPerfilId(), item.getTipoItem().getId());
    }
}
