package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.EmailIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailIdentidadExisteFinderImpl implements EmailIdentidadExisteFinder {

    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;

    @Override
    public Boolean obtener(String email) {
        return proveedorIdentidadOutputPort.existeEmail(email);
    }
}
