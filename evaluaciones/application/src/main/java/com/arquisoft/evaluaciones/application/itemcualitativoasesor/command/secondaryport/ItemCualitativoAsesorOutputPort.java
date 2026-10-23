package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport;

import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoAsesorOutputPort {

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);
}
