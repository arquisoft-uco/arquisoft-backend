package com.arquisoft.biblioteca.application.documentacion.query.readmodel;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel.BibliotecarioResumen;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel.EntregableProyectoGradoResumen;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel.EstadoDocumentacionResumen;

import java.util.UUID;

public record EnlaceDocumentacionReadModel(
        String enlace,
        UUID id,
        EntregableProyectoGradoResumen entregableProyectoGrado,
        BibliotecarioResumen bibliotecario,
        EstadoDocumentacionResumen estadoDocumentacion
) {
}
