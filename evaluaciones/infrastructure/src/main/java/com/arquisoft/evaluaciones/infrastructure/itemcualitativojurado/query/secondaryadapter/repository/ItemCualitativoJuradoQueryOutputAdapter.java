package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.secondaryport.ItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository.mapper.ItemCualitativoJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemCualitativoJuradoQueryOutputAdapter implements ItemCualitativoJuradoQueryOutputPort {

    private final ItemCualitativoJuradoQueryRepository repository;

    @Override
    public List<ItemCualitativoJuradoReadModel> consultarTodos() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(ItemCualitativoJuradoQueryMapper::toReadModel)
                .toList();
    }
}
