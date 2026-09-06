package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCualitativoJuradoExisteFinderImpl implements ItemCualitativoJuradoExisteFinder {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;

    @Override
    public Boolean obtener(UUID itemCualitativoJurado) {
        return itemCualitativoJuradoOutputPort.existePorId(itemCualitativoJurado);
    }
}
