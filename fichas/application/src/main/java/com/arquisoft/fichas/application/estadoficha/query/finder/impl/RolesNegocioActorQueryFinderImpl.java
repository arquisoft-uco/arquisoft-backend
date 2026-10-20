package com.arquisoft.fichas.application.estadoficha.query.finder.impl;

import com.arquisoft.fichas.application.estadoficha.query.finder.RolesNegocioActorQueryFinder;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.RolesNegocioActorQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RolesNegocioActorQueryFinderImpl implements RolesNegocioActorQueryFinder {

    private final RolesNegocioActorQueryOutputPort rolesNegocioActorQueryOutputPort;

    @Override
    public List<String> obtener(UUID actor) {
        return rolesNegocioActorQueryOutputPort.consultarPorActor(actor);
    }
}
