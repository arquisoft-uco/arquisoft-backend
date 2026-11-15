package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.minio;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.EnlaceDescargaDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.documentacion.exception.ArchivoDocumentacionAusenteException;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnlaceDescargaDocumentacionMinioQueryOutputAdapter
        implements EnlaceDescargaDocumentacionQueryOutputPort {

    private static final String SEPARADOR_CLAVE = "/";

    private final AlmacenamientoObjetos almacenamientoObjetos;

    @Override
    public EnlaceDescargaDocumentacionReadModel generar(
            UUID documentacion, String bucket, String claveObjeto, String nombreArchivo) {
        var objeto = almacenamientoObjetos.consultarMetadatos(bucket, claveObjeto);
        if (objeto.esVacio()) {
            throw new ArchivoDocumentacionAusenteException(documentacion, bucket, claveObjeto);
        }

        var nombre = UtilTexto.esVacioONulo(nombreArchivo)
                ? claveObjeto.substring(claveObjeto.lastIndexOf(SEPARADOR_CLAVE) + 1)
                : nombreArchivo;

        var enlace = almacenamientoObjetos.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(bucket, claveObjeto, nombre));
        return new EnlaceDescargaDocumentacionReadModel(enlace.url(), enlace.expiraEn());
    }
}
