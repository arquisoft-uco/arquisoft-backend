package com.arquisoft.fichas.application.estadoficha.query.secondaryport;

import java.util.List;
import java.util.UUID;

public interface RolesNegocioActorQueryOutputPort {

    List<String> consultarPorActor(UUID actor);
}
