package com.arquisoft.biblioteca.application.documentacion.command.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionPorIdFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.mapper.DocumentacionMapper;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentacionPorIdFinderImpl implements DocumentacionPorIdFinder {

    private final DocumentacionOutputPort documentacionOutputPort;

    @Override
    public DocumentacionDomain obtener(UUID id) {
        return documentacionOutputPort.obtenerPorId(id)
                .map(DocumentacionMapper::toDomain)
                .orElse(DocumentacionDomain.VACIO);
    }
}
