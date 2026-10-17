package com.arquisoft.biblioteca.application.bibliotecario.command.finder.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.mapper.BibliotecarioMapper;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BibliotecarioPorIdFinderImpl implements BibliotecarioPorIdFinder {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;

    @Override
    public BibliotecarioDomain obtener(UUID id) {
        return bibliotecarioOutputPort.obtenerPorId(id)
                .map(BibliotecarioMapper::toDomain)
                .orElse(BibliotecarioDomain.VACIO);
    }
}
