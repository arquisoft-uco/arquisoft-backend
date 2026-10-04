package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.secondaryport.CriterioItemCualitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CriterioItemCualitativoJuradoCommandOutputAdapter implements CriterioItemCualitativoJuradoOutputPort {

    private final CriterioItemCualitativoJuradoCommandRepository repository;

    @Override
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        return repository.findIdsByIdIn(ids);
    }
}
