package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemsCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCualitativosAsesorExistentesFinderImpl implements ItemsCualitativosAsesorExistentesFinder {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;

    @Override
    public Set<UUID> obtener(Set<UUID> ids) {
        return itemCualitativoAsesorOutputPort.consultarIdsExistentes(ids);
    }
}
