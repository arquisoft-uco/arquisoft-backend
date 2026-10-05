package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.secondaryport.ItemCuantitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository.mapper.ItemCuantitativoJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoJuradoQueryOutputAdapter implements ItemCuantitativoJuradoQueryOutputPort {

    private final ItemCuantitativoJuradoQueryRepository repository;

    @Override
    public List<ItemCuantitativoJuradoReadModel> consultarTodos() {
        return repository.findAllByOrderByCategoriaIdAscNombreAsc()
                .stream()
                .map(ItemCuantitativoJuradoQueryMapper::toReadModel)
                .toList();
    }
}
