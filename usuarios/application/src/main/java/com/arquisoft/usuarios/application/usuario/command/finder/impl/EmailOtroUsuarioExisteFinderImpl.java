package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOtroUsuarioExisteFinderImpl implements EmailOtroUsuarioExisteFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Boolean obtener(UnicidadOtroUsuario unicidad) {
        return usuarioOutputPort.existePorEmailEnOtroUsuario(unicidad.valor(), unicidad.usuario());
    }
}
