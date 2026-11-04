package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.secondaryport.ItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository.mapper.ItemCuantitativoAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoAsesorQueryOutputAdapter implements ItemCuantitativoAsesorQueryOutputPort {

    private final ItemCuantitativoAsesorQueryRepository repository;

    @Override
    public List<ItemCuantitativoAsesorReadModel> consultarTodos() {
        return repository.findAllByOrderByCategoriaIdAscNombreAsc()
                .stream()
                .map(ItemCuantitativoAsesorQueryMapper::toReadModel)
                .toList();
    }
}
