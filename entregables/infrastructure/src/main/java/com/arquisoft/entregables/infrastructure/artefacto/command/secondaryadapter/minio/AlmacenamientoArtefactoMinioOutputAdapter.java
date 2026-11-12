package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.minio;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArchivoOrigenArtefactoEntity;
import com.arquisoft.entregables.infrastructure.config.EntregablesAlmacenamientoProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlmacenamientoArtefactoMinioOutputAdapter implements AlmacenamientoArtefactoOutputPort {

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final EntregablesAlmacenamientoProperties propiedades;
    private final AppLogger logger;

    @Override
    public Optional<ArchivoOrigenArtefactoEntity> obtenerMetadatosOrigen(String clave) {
        var metadatos = almacenamientoObjetos.consultarMetadatos(propiedades.getBucket(), clave);
        if (metadatos.esVacio()) {
            return Optional.empty();
        }
        return Optional.of(new ArchivoOrigenArtefactoEntity(clave, metadatos.etag()));
    }

    @Override
    public void copiar(String claveOrigen, String claveDestino, String etag) {
        var bucket = propiedades.getBucket();
        almacenamientoObjetos.copiar(bucket, claveOrigen, claveDestino, etag);
        logger.debug(ArtefactoKey.LOG_COPIADO, bucket, claveOrigen, claveDestino);
    }

    @Override
    public void eliminar(String clave) {
        var bucket = propiedades.getBucket();
        almacenamientoObjetos.eliminar(bucket, clave);
        logger.debug(ArtefactoKey.LOG_COPIA_ANTERIOR_ELIMINADA, bucket, clave);
    }
}
