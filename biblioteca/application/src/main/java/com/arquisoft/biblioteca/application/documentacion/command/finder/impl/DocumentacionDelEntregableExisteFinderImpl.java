package com.arquisoft.biblioteca.application.documentacion.command.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionDelEntregableExisteFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentacionDelEntregableExisteFinderImpl implements DocumentacionDelEntregableExisteFinder {

    private final DocumentacionOutputPort documentacionOutputPort;

    @Override
    public Boolean obtener(UUID entregableProyectoGrado) {
        return documentacionOutputPort.existePorEntregableProyectoGrado(entregableProyectoGrado);
    }
}
