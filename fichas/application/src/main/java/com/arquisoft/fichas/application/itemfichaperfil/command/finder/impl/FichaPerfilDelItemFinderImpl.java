package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilDelItemFinderImpl implements FichaPerfilDelItemFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public Optional<UUID> obtener(UUID item) {
        return itemFichaPerfilOutputPort.obtenerFichaPerfilId(item);
    }
}
