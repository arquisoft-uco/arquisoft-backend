package com.arquisoft.usuarios.application.bibliotecario.command.finder.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.finder.BibliotecarioPorUsuarioFinder;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.mapper.BibliotecarioMapper;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BibliotecarioPorUsuarioFinderImpl implements BibliotecarioPorUsuarioFinder {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;

    @Override
    public BibliotecarioDomain obtener(UUID usuario) {
        return bibliotecarioOutputPort.obtenerPorUsuario(usuario).map(BibliotecarioMapper::toDomain)
                .orElse(BibliotecarioDomain.VACIO);
    }
}
