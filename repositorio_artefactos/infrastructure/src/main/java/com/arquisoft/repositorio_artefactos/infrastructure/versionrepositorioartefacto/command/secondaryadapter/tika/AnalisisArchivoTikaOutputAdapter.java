package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.tika;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosAlmacenamiento;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.tika.InspectorArchivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalisisArchivoTikaOutputAdapter implements AnalisisArchivoOutputPort {

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final RepositorioArtefactosAlmacenamientoProperties propiedades;
    private final InspectorArchivo inspectorArchivo;

    @Override
    public Optional<AnalisisArchivoEntity> analizarCarga(UUID carga) {
        return almacenamientoObjetos
                .abrirLectura(propiedades.getBucket(), RepositorioArtefactosAlmacenamiento.PREFIJO_PENDIENTES + carga)
                .map(this::analizar);
    }

    private AnalisisArchivoEntity analizar(LecturaObjeto lectura) {
        try (lectura) {
            var inspeccion = inspectorArchivo.inspeccionar(lectura.contenido(), propiedades.getTamanoMaximoBytes());
            return new AnalisisArchivoEntity(inspeccion.tipoDetectado(), inspeccion.hashSha256(), lectura.etag());
        } catch (IOException e) {
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA), AppCodes.Minio.LECTURA_FALLIDA, e);
        }
    }
}
