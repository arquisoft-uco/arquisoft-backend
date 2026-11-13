package com.arquisoft.biblioteca.application.documentacion.query.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.query.finder.EstadoDeDocumentacionQueryFinder;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionAccesoQueryOutputPort;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoDeDocumentacionQueryFinderImpl implements EstadoDeDocumentacionQueryFinder {

    private final DocumentacionAccesoQueryOutputPort documentacionAccesoQueryOutputPort;

    @Override
    public EstadoDocumentacion obtener(UUID documentacion) {
        return documentacionAccesoQueryOutputPort.obtenerEstadoPorId(documentacion)
                .map(EstadoDocumentacion::desde)
                .orElse(EstadoDocumentacion.VACIO);
    }
}
