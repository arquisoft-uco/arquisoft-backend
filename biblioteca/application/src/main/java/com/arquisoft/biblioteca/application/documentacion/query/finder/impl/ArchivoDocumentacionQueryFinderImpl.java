package com.arquisoft.biblioteca.application.documentacion.query.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.query.finder.ArchivoDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.ArchivoDocumentacionAccesoQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.mapper.ArchivoDocumentacionAccesoMapper;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArchivoDocumentacionQueryFinderImpl implements ArchivoDocumentacionQueryFinder {

    private final ArchivoDocumentacionAccesoQueryOutputPort archivoDocumentacionAccesoQueryOutputPort;

    @Override
    public ArchivoDocumentacion obtener(UUID documentacion) {
        return archivoDocumentacionAccesoQueryOutputPort.obtenerPorId(documentacion)
                .map(ArchivoDocumentacionAccesoMapper::toDomain)
                .orElse(ArchivoDocumentacion.VACIO);
    }
}
