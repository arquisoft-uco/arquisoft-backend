package com.arquisoft.biblioteca.application.autor.command.finder.impl;

import com.arquisoft.biblioteca.application.autor.command.finder.AutorPorIdFinder;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.mapper.AutorMapper;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutorPorIdFinderImpl implements AutorPorIdFinder {

    private final AutorOutputPort autorOutputPort;

    @Override
    public AutorDomain obtener(UUID id) {
        return autorOutputPort.obtenerPorId(id)
                .map(AutorMapper::toDomain)
                .orElse(AutorDomain.VACIO);
    }
}
