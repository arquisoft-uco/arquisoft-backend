package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.tika;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.constant.ArtefactosAlmacenamiento;
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
    private final ArtefactosAlmacenamientoProperties propiedades;
    private final InspectorArchivo inspectorArchivo;

    @Override
    public Optional<AnalisisArchivoEntity> analizarCarga(UUID carga) {
        return almacenamientoObjetos
                .abrirLectura(propiedades.getBucket(), ArtefactosAlmacenamiento.PREFIJO_PENDIENTES + carga)
                .map(this::analizar);
    }

    private AnalisisArchivoEntity analizar(LecturaObjeto lectura) {
        try (lectura) {
            var inspeccion = inspectorArchivo.inspeccionar(lectura.contenido(), propiedades.getTamanoMaximoBytes());
            return new AnalisisArchivoEntity(inspeccion.tipoDetectado(), lectura.etag());
        } catch (IOException e) {
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA), AppCodes.Minio.LECTURA_FALLIDA, e);
        }
    }
}
