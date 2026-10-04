package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtraIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOtraIdentidadExisteFinderImpl implements EmailOtraIdentidadExisteFinder {

    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;

    @Override
    public Boolean obtener(UnicidadOtroUsuario unicidad) {
        return proveedorIdentidadOutputPort.existeEmailEnOtraIdentidad(unicidad.valor(), unicidad.usuario());
    }
}
