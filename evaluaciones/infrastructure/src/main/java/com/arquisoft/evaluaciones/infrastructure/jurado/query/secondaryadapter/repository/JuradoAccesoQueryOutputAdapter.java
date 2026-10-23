package com.arquisoft.evaluaciones.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.jurado.query.secondaryport.JuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoAccesoQueryOutputAdapter implements JuradoAccesoQueryOutputPort {

    private final JuradoAccesoQueryRepository repository;

    @Override
    public boolean existePorId(UUID jurado) {
        return repository.existsById(jurado);
    }
}
