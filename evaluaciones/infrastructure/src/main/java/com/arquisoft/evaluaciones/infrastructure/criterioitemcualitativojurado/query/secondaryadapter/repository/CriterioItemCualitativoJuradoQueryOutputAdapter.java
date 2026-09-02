package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.secondaryport.CriterioItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository.mapper.CriterioItemCualitativoJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CriterioItemCualitativoJuradoQueryOutputAdapter implements CriterioItemCualitativoJuradoQueryOutputPort {

    private final CriterioItemCualitativoJuradoQueryRepository repository;

    @Override
    public List<CriterioItemCualitativoJuradoReadModel> consultarTodos() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(CriterioItemCualitativoJuradoQueryMapper::toReadModel)
                .toList();
    }
}
