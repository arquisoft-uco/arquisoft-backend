package com.arquisoft.fichas.infrastructure.tipoitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.tipoitem.query.secondaryport.TipoItemQueryOutputPort;
import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.tipoitem.query.secondaryadapter.repository.mapper.TipoItemQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TipoItemQueryOutputAdapter implements TipoItemQueryOutputPort {

    private final TipoItemQueryRepository repository;

    @Override
    public List<TipoItemReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(TipoItemQueryMapper::toReadModel)
                .toList();
    }
}
