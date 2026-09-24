package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.IdentificadorOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentificadorOtroUsuarioExisteFinderImpl implements IdentificadorOtroUsuarioExisteFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Boolean obtener(UnicidadOtroUsuario unicidad) {
        return usuarioOutputPort.existePorIdentificadorEnOtroUsuario(unicidad.valor(), unicidad.usuario());
    }
}
