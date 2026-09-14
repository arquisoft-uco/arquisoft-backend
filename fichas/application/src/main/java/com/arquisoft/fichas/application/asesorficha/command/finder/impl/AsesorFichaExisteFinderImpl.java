package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaExisteFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaExisteFinderImpl implements AsesorFichaExisteFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public Boolean obtener(UUID asesorFicha) {
        return asesorFichaOutputPort.existePorId(asesorFicha);
    }
}
