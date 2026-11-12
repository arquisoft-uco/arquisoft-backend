package com.arquisoft.entregables.application.artefacto.command.secondaryport;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArchivoOrigenArtefactoEntity;

import java.util.Optional;

public interface AlmacenamientoArtefactoOutputPort {

    Optional<ArchivoOrigenArtefactoEntity> obtenerMetadatosOrigen(String clave);

    void copiar(String claveOrigen, String claveDestino, String etag);

    void eliminar(String clave);
}
