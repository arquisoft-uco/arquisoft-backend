package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ValoresMaximosItemsCuantitativosAsesorFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ValoresMaximosItemsCuantitativosAsesorFinderImpl implements ValoresMaximosItemsCuantitativosAsesorFinder {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;

    @Override
    public Map<UUID, Integer> obtener(Set<UUID> ids) {
        return itemCuantitativoAsesorOutputPort.consultarValoresMaximosPorIds(ids);
    }
}
