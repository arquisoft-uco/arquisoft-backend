package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.secondaryport.CriterioItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository.mapper.CriterioItemCualitativoAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CriterioItemCualitativoAsesorQueryOutputAdapter implements CriterioItemCualitativoAsesorQueryOutputPort {

    private final CriterioItemCualitativoAsesorQueryRepository repository;

    @Override
    public List<CriterioItemCualitativoAsesorReadModel> consultarTodos() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(CriterioItemCualitativoAsesorQueryMapper::toReadModel)
                .toList();
    }
}
