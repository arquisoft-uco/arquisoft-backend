package com.arquisoft.usuarios.application.administrador.command.finder.impl;

import com.arquisoft.usuarios.application.administrador.command.finder.AdministradoresVigentesCountFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdministradoresVigentesCountFinderImpl implements AdministradoresVigentesCountFinder {

    private final AdministradorOutputPort administradorOutputPort;

    @Override
    public Long obtener() {
        return administradorOutputPort.contarVigentes();
    }
}
