package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCualitativaJuradoOutputPort {

    void registrarTodas(List<EvaluacionCualitativaJuradoEntity> entidades);

    Set<UUID> consultarItemsRegistrados(UUID evaluacionJurado, Set<UUID> items);

    boolean existePorItem(UUID item);
}
