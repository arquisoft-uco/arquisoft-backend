package com.arquisoft.usuarios.application.asesorficha.command.finder.impl;

import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaPorUsuarioFinderImpl implements AsesorFichaPorUsuarioFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public AsesorFichaDomain obtener(UUID usuario) {
        return asesorFichaOutputPort.obtenerPorUsuario(usuario).map(AsesorFichaMapper::toDomain)
                .orElse(AsesorFichaDomain.VACIO);
    }
}
