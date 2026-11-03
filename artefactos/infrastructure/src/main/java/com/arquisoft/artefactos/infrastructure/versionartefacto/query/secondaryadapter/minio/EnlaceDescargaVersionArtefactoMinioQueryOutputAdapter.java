package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.minio;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.EnlaceDescargaVersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.artefactos.infrastructure.versionartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnlaceDescargaVersionArtefactoMinioQueryOutputAdapter
        implements EnlaceDescargaVersionArtefactoQueryOutputPort {

    private static final String METADATO_NOMBRE_ARCHIVO = "nombre-archivo";

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final ArtefactosAlmacenamientoProperties propiedades;

    @Override
    public EnlaceDescargaVersionArtefactoReadModel generar(
            UUID versionArtefacto, String uriArchivo, String nombreRespaldo) {
        var objeto = almacenamientoObjetos.consultarMetadatos(propiedades.getBucket(), uriArchivo);
        if (objeto.esVacio()) {
            throw new ArchivoVersionNoDisponibleException(versionArtefacto, uriArchivo);
        }

        var nombreSubido = objeto.metadatos().get(METADATO_NOMBRE_ARCHIVO);
        var nombre = UtilTexto.esVacioONulo(nombreSubido) ? nombreRespaldo : nombreSubido;

        var enlace = almacenamientoObjetos.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(propiedades.getBucket(), uriArchivo, nombre));
        return new EnlaceDescargaVersionArtefactoReadModel(enlace.url(), enlace.expiraEn());
    }
}
