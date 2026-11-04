package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ItemCuantitativoAsesorOutputPort {

    Map<UUID, Integer> consultarValoresMaximosPorIds(Set<UUID> ids);
}
