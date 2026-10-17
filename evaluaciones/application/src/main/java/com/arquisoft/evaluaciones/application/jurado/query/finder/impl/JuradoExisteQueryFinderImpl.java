package com.arquisoft.evaluaciones.application.jurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.jurado.query.finder.JuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.jurado.query.secondaryport.JuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoExisteQueryFinderImpl implements JuradoExisteQueryFinder {

    private final JuradoAccesoQueryOutputPort juradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(UUID jurado) {
        return juradoAccesoQueryOutputPort.existePorId(jurado);
    }
}
