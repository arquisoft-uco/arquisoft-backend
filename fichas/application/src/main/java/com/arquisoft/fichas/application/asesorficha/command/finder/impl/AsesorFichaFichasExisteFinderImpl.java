package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFichasExisteFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaFichasExisteFinderImpl implements AsesorFichaFichasExisteFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public Boolean obtener(UUID asesorFicha) {
        return asesorFichaOutputPort.existePorId(asesorFicha);
    }
}
