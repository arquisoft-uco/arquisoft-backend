package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.ContactoOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactoOtroUsuarioExisteFinderImpl implements ContactoOtroUsuarioExisteFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Boolean obtener(UnicidadOtroUsuario unicidad) {
        return usuarioOutputPort.existePorContactoEnOtroUsuario(unicidad.valor(), unicidad.usuario());
    }
}
