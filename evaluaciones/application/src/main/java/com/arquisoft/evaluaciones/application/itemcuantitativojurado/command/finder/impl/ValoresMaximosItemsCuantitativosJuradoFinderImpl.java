package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ValoresMaximosItemsCuantitativosJuradoFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ValoresMaximosItemsCuantitativosJuradoFinderImpl implements ValoresMaximosItemsCuantitativosJuradoFinder {

    private final ItemCuantitativoJuradoOutputPort itemCuantitativoJuradoOutputPort;

    @Override
    public Map<UUID, Integer> obtener(Set<UUID> ids) {
        return itemCuantitativoJuradoOutputPort.consultarValoresMaximosPorIds(ids);
    }
}
