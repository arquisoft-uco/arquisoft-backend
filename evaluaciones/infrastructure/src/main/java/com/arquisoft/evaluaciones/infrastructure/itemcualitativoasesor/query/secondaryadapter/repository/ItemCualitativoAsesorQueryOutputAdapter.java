package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.secondaryport.ItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository.mapper.ItemCualitativoAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemCualitativoAsesorQueryOutputAdapter implements ItemCualitativoAsesorQueryOutputPort {

    private final ItemCualitativoAsesorQueryRepository repository;

    @Override
    public List<ItemCualitativoAsesorReadModel> consultarTodos() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(ItemCualitativoAsesorQueryMapper::toReadModel)
                .toList();
    }
}
