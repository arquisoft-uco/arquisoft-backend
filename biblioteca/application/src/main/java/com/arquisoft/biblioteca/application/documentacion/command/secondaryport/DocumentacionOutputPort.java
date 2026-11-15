package com.arquisoft.biblioteca.application.documentacion.command.secondaryport;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;

import java.util.Optional;
import java.util.UUID;

public interface DocumentacionOutputPort {

    Optional<DocumentacionEntity> obtenerPorId(UUID id);

    void actualizarEstado(UUID documentacion, String estadoDocumentacion);
}
