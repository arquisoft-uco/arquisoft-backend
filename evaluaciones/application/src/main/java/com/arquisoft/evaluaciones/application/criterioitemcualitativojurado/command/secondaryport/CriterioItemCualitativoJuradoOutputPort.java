package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.secondaryport;

import java.util.Set;
import java.util.UUID;

public interface CriterioItemCualitativoJuradoOutputPort {

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);
}
