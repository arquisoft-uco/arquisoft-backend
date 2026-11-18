package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.mapper.ItemCuantitativoAsesorMapper;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoAsesorPorIdFinderImpl implements ItemCuantitativoAsesorPorIdFinder {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;

    @Override
    public ItemCuantitativoAsesorDomain obtener(UUID id) {
        return itemCuantitativoAsesorOutputPort.obtenerPorId(id)
                .map(ItemCuantitativoAsesorMapper::toDomain)
                .orElse(ItemCuantitativoAsesorDomain.VACIO);
    }
}
