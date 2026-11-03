package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.minio;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.EnlaceDescargaVersionQueryOutputPort;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnlaceDescargaVersionMinioQueryOutputAdapter implements EnlaceDescargaVersionQueryOutputPort {

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final RepositorioArtefactosAlmacenamientoProperties propiedades;

    @Override
    public EnlaceDescargaVersionReadModel generar(UUID version, String uriArchivo, String nombreArchivo) {
        var metadatos = almacenamientoObjetos.consultarMetadatos(propiedades.getBucket(), uriArchivo);
        if (metadatos.esVacio()) {
            throw new ArchivoVersionNoDisponibleException(version, uriArchivo);
        }

        var enlace = almacenamientoObjetos.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(propiedades.getBucket(), uriArchivo, nombreArchivo));
        return new EnlaceDescargaVersionReadModel(enlace.url(), enlace.expiraEn());
    }
}
