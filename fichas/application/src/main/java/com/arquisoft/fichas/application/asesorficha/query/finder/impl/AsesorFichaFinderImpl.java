package com.arquisoft.fichas.application.asesorficha.query.finder.impl;

import com.arquisoft.fichas.application.asesorficha.query.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaFinderImpl implements AsesorFichaFinder {

    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Override
    public AsesorContactoReadModel obtener(UUID asesorFichaId) {
        return asesorFichaQueryOutputPort.buscarContactoPorId(asesorFichaId)
                .orElseThrow(() -> new AsesorFichaNoEncontradoException(asesorFichaId));
    }
}
