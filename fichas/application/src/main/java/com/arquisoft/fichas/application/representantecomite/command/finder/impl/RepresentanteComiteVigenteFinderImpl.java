package com.arquisoft.fichas.application.representantecomite.command.finder.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteVigenteFinder;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteVigenteFinderImpl implements RepresentanteComiteVigenteFinder {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;

    @Override
    public Boolean obtener(UUID representanteComite) {
        return representanteComiteOutputPort.existeVigentePorId(representanteComite);
    }
}
