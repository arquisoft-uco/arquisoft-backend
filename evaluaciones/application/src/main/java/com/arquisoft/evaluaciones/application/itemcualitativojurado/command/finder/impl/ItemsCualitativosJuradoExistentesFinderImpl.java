package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemsCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCualitativosJuradoExistentesFinderImpl implements ItemsCualitativosJuradoExistentesFinder {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;

    @Override
    public Set<UUID> obtener(Set<UUID> ids) {
        return itemCualitativoJuradoOutputPort.consultarIdsExistentes(ids);
    }
}
