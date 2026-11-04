package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.secondaryport.CriterioItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CriterioItemCualitativoAsesorCommandOutputAdapter implements CriterioItemCualitativoAsesorOutputPort {

    private final CriterioItemCualitativoAsesorCommandRepository repository;

    @Override
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        return repository.findIdsByIdIn(ids);
    }
}
