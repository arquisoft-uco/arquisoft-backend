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
import lombok.RequiredArgsConstructor;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalisisArchivoTikaOutputAdapter implements AnalisisArchivoOutputPort {

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final RepositorioArtefactosAlmacenamientoProperties propiedades;
    private final Detector detector = new DefaultDetector();

    @Override
    public Optional<AnalisisArchivoEntity> analizarCarga(UUID carga) {
        return almacenamientoObjetos
                .abrirLectura(propiedades.getBucket(), RepositorioArtefactosAlmacenamiento.PREFIJO_PENDIENTES + carga)
                .map(this::analizar);
    }

    private AnalisisArchivoEntity analizar(LecturaObjeto lectura) {
        try (lectura) {
            var inspector = new InspectorFlujoArchivo(lectura.contenido(), propiedades.getTamanoMaximoBytes());
            String tipoDetectado;
            try (var flujo = TikaInputStream.get(inspector)) {
                tipoDetectado = detector.detect(flujo, new Metadata()).toString();
                inspector.drenar();
            }
            return new AnalisisArchivoEntity(tipoDetectado, inspector.hashHexadecimal(), lectura.etag());
        } catch (IOException e) {
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA), AppCodes.Minio.LECTURA_FALLIDA, e);
        }
    }
}
