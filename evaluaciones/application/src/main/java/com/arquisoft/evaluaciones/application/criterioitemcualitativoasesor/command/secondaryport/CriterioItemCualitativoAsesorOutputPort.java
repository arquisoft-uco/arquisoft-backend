package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.secondaryport;

import java.util.Set;
import java.util.UUID;

public interface CriterioItemCualitativoAsesorOutputPort {

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);
}
