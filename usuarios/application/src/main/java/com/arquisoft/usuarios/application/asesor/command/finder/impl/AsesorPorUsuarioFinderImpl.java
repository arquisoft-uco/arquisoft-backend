package com.arquisoft.usuarios.application.asesor.command.finder.impl;

import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorPorUsuarioFinderImpl implements AsesorPorUsuarioFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public AsesorDomain obtener(UUID usuario) {
        return asesorOutputPort.obtenerPorUsuario(usuario).map(AsesorMapper::toDomain)
                .orElse(AsesorDomain.VACIO);
    }
}
