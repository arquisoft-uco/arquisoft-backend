package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.query.secondaryport.RolesNegocioActorQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RolesNegocioActorQueryOutputAdapter implements RolesNegocioActorQueryOutputPort {

    private final ActorRolNegocioQueryRepository repository;

    @Override
    public List<String> consultarPorActor(UUID actor) {
        return repository.findRolesByActor(actor);
    }
}
