package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorDeFichaFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorDeFichaFinderImpl implements AsesorDeFichaFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public AsesorFichaDomain obtener(UUID fichaPerfil) {
        return asesorFichaOutputPort.obtenerPorFichaPerfil(fichaPerfil)
                .map(AsesorFichaMapper::toDomain)
                .orElse(AsesorFichaDomain.VACIO);
    }
}
