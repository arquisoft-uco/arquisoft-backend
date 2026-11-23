package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.minio;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.AlmacenamientoDocumentacionOutputPort;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaAlmacenamientoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlmacenamientoDocumentacionMinioOutputAdapter implements AlmacenamientoDocumentacionOutputPort {

    private final BibliotecaAlmacenamientoProperties propiedades;

    @Override
    public String bucketEntregables() {
        return propiedades.getBucketEntregables();
    }
}
