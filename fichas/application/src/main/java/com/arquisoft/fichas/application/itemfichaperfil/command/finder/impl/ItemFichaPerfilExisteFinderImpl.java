package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilExisteFinderImpl implements ItemFichaPerfilExisteFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public Boolean obtener(UUID item) {
        return itemFichaPerfilOutputPort.existePorId(item);
    }
}
