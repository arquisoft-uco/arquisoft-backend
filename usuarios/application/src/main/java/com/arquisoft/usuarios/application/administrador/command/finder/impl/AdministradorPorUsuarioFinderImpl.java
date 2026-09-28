package com.arquisoft.usuarios.application.administrador.command.finder.impl;

import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdministradorPorUsuarioFinderImpl implements AdministradorPorUsuarioFinder {

    private final AdministradorOutputPort administradorOutputPort;

    @Override
    public AdministradorDomain obtener(UUID usuario) {
        return administradorOutputPort.obtenerPorUsuario(usuario).map(AdministradorMapper::toDomain)
                .orElse(AdministradorDomain.VACIO);
    }
}
