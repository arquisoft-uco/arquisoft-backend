package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.minio;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.ArtefactosAlmacenamiento;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmacenamientoVersionMinioOutputAdapter implements AlmacenamientoVersionOutputPort {

    private static final String METADATO_SOLICITANTE = "solicitante";
    private static final String METADATO_ARTEFACTO = "artefacto";
    private static final String METADATO_NOMBRE_ARCHIVO = "nombre-archivo";

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final ArtefactosAlmacenamientoProperties propiedades;
    private final AppLogger logger;

    @Override
    public FormularioCarga generarFormularioCarga(SolicitudCargaEntity solicitud) {
        var metadatos = new LinkedHashMap<String, String>();
        metadatos.put(METADATO_SOLICITANTE, solicitud.solicitante().toString());
        metadatos.put(METADATO_ARTEFACTO, solicitud.artefacto().toString());
        metadatos.put(METADATO_NOMBRE_ARCHIVO, solicitud.nombreArchivo());

        var formulario = almacenamientoObjetos.generarFormularioCarga(new SolicitudFormularioCarga(
                propiedades.getBucket(),
                keyPendiente(solicitud.carga()),
                solicitud.tipoContenido(),
                propiedades.getTamanoMaximoBytes(),
                metadatos));
        return new FormularioCarga(formulario.url(), formulario.campos(), formulario.expiraEn());
    }

    @Override
    public Optional<CargaPendienteEntity> obtenerCargaPendiente(UUID carga) {
        var objeto = almacenamientoObjetos.consultarMetadatos(propiedades.getBucket(), keyPendiente(carga));
        if (objeto.esVacio()) {
            return Optional.empty();
        }
        var metadatos = objeto.metadatos();
        return Optional.of(new CargaPendienteEntity(
                carga,
                uuidODefecto(metadatos.get(METADATO_ARTEFACTO)),
                uuidODefecto(metadatos.get(METADATO_SOLICITANTE)),
                metadatos.getOrDefault(METADATO_NOMBRE_ARCHIVO, UtilTexto.VACIO),
                objeto.tipoContenido(),
                objeto.tamano(),
                objeto.etag()));
    }

    @Override
    public void copiarCargaAUri(UUID carga, String etagEsperado, String uriArchivo) {
        almacenamientoObjetos.copiar(propiedades.getBucket(), keyPendiente(carga), uriArchivo, etagEsperado);
        logger.debug(VersionArtefactoKey.LOG_CARGA_COPIADA, carga, uriArchivo);
    }

    @Override
    public void eliminarCarga(UUID carga) {
        almacenamientoObjetos.eliminar(propiedades.getBucket(), keyPendiente(carga));
        logger.debug(VersionArtefactoKey.LOG_CARGA_ELIMINADA, carga);
    }

    @Override
    public long tamanoMaximoBytes() {
        return propiedades.getTamanoMaximoBytes();
    }

    private static String keyPendiente(UUID carga) {
        return ArtefactosAlmacenamiento.PREFIJO_PENDIENTES + carga;
    }

    private static UUID uuidODefecto(String texto) {
        return UtilObjeto.aplicarPorDefecto(UtilUUID.generarUUIDDesdeTexto(texto), UtilUUID.obtenerUUIDPorDefecto());
    }
}
