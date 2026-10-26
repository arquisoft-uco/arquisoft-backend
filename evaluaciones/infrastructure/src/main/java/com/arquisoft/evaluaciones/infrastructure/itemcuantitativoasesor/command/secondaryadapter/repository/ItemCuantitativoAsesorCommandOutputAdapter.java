package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.projection.ValorMaximoItemCuantitativoAsesorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoAsesorCommandOutputAdapter implements ItemCuantitativoAsesorOutputPort {

    private final ItemCuantitativoAsesorCommandRepository repository;

    @Override
    public Map<UUID, Integer> consultarValoresMaximosPorIds(Set<UUID> ids) {
        return repository.findValoresMaximosByIdIn(ids).stream()
                .collect(Collectors.toUnmodifiableMap(
                        ValorMaximoItemCuantitativoAsesorProjection::getId,
                        ValorMaximoItemCuantitativoAsesorProjection::getValor));
    }
}
