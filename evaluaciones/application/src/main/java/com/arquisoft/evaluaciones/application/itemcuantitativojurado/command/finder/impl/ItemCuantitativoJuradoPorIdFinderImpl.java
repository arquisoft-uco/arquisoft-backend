package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.mapper.ItemCuantitativoJuradoMapper;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoJuradoPorIdFinderImpl implements ItemCuantitativoJuradoPorIdFinder {

    private final ItemCuantitativoJuradoOutputPort outputPort;

    @Override
    public Optional<ItemCuantitativoJuradoDomain> obtener(UUID id) {
        return outputPort.obtenerPorId(id).map(ItemCuantitativoJuradoMapper::toDomain);
    }
}
