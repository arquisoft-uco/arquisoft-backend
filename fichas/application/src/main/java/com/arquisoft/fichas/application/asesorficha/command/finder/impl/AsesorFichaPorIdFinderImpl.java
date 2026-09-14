package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaPorIdFinderImpl implements AsesorFichaPorIdFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public Optional<AsesorFichaEntity> obtener(UUID id) {
        return asesorFichaOutputPort.obtenerPorId(id);
    }
}
