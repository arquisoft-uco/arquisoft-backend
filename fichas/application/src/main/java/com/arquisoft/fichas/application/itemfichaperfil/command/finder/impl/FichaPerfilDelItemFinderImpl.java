package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilDelItemFinderImpl implements FichaPerfilDelItemFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public UUID obtener(UUID item) {
        return itemFichaPerfilOutputPort.obtenerFichaPerfilId(item)
                .orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(item));
    }
}
