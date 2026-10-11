package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoCommandOutputAdapter implements JuradoOutputPort {

    private final JuradoCommandRepository repository;

    @Override
    public Set<UUID> consultarVigentesPorIds(Set<UUID> jurados) {
        return repository.findIdsVigentes(jurados);
    }
}
