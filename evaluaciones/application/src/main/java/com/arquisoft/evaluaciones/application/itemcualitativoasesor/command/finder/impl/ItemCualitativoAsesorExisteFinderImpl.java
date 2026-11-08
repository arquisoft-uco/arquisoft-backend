package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCualitativoAsesorExisteFinderImpl implements ItemCualitativoAsesorExisteFinder {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;

    @Override
    public Boolean obtener(UUID itemCualitativoAsesor) {
        return itemCualitativoAsesorOutputPort.existePorId(itemCualitativoAsesor);
    }
}
