package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCualitativoAsesorCommandOutputAdapter implements ItemCualitativoAsesorOutputPort {

    private final ItemCualitativoAsesorCommandRepository repository;

    @Override
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        return repository.findIdsByIdIn(ids);
    }
}
