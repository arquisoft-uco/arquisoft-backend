package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.model.FormularioCarga;

import java.util.Optional;
import java.util.UUID;

public interface AlmacenamientoVersionOutputPort {

    FormularioCarga generarFormularioCarga(SolicitudCargaEntity solicitud);

    Optional<CargaPendienteEntity> obtenerCargaPendiente(UUID carga);

    void copiarCargaAUri(UUID carga, String etagEsperado, String uriArchivo);

    void eliminarCarga(UUID carga);

    long tamanoMaximoBytes();
}
