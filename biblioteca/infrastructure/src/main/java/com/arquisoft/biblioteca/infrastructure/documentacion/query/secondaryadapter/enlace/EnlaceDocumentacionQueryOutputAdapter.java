package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.enlace;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaEnlacesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnlaceDocumentacionQueryOutputAdapter implements EnlaceDocumentacionQueryOutputPort {

    private static final String SEPARADOR = "/";
    private static final String MARCADOR_DOCUMENTACION = "{documentacionId}";

    private final BibliotecaEnlacesProperties enlacesProperties;

    @Override
    public String generar(UUID documentacion) {
        var urlBase = enlacesProperties.getUrlBase();
        var base = urlBase.endsWith(SEPARADOR) ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
        return base + enlacesProperties.getRutaDocumentacion()
                .replace(MARCADOR_DOCUMENTACION, documentacion.toString());
    }
}
