package com.arquisoft.repositorio_artefactos.application.administrador.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdministradorPorIdFinderImpl implements AdministradorPorIdFinder {

    private final AdministradorOutputPort administradorOutputPort;

    @Override
    public AdministradorDomain obtener(UUID id) {
        return administradorOutputPort.obtenerPorId(id)
                .map(AdministradorMapper::toDomain)
                .orElse(AdministradorDomain.VACIO);
    }
}
