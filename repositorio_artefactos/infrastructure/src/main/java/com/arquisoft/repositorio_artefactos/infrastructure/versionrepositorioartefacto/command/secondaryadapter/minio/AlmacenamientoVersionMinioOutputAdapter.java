package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.minio;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.RepositorioArtefactosAlmacenamiento;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmacenamientoVersionMinioOutputAdapter implements AlmacenamientoVersionOutputPort {

    private static final String METADATO_SOLICITANTE = "solicitante";
    private static final String METADATO_REPOSITORIO = "repositorio";
    private static final String METADATO_NOMBRE_ARCHIVO = "nombre-archivo";

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final RepositorioArtefactosAlmacenamientoProperties propiedades;
    private final AppLogger logger;

    @Override
    public FormularioCarga generarFormularioCarga(SolicitudCargaEntity solicitud) {
        var metadatos = new LinkedHashMap<String, String>();
        metadatos.put(METADATO_SOLICITANTE, solicitud.solicitante().toString());
        metadatos.put(METADATO_REPOSITORIO, solicitud.repositorio().toString());
        metadatos.put(METADATO_NOMBRE_ARCHIVO, URLEncoder.encode(solicitud.nombreArchivo(), StandardCharsets.UTF_8));

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
                uuidODefecto(metadatos.get(METADATO_REPOSITORIO)),
                uuidODefecto(metadatos.get(METADATO_SOLICITANTE)),
                decodificar(metadatos.get(METADATO_NOMBRE_ARCHIVO)),
                objeto.tipoContenido(),
                objeto.tamano(),
                objeto.etag()));
    }

    @Override
    public void copiarCargaAUri(UUID carga, String etagEsperado, String uriArchivo) {
        almacenamientoObjetos.copiar(propiedades.getBucket(), keyPendiente(carga), uriArchivo, etagEsperado);
        logger.debug(VersionRepositorioArtefactoKey.LOG_CARGA_COPIADA, carga, uriArchivo);
    }

    @Override
    public void eliminarCarga(UUID carga) {
        almacenamientoObjetos.eliminar(propiedades.getBucket(), keyPendiente(carga));
        logger.debug(VersionRepositorioArtefactoKey.LOG_CARGA_ELIMINADA, carga);
    }

    @Override
    public long tamanoMaximoBytes() {
        return propiedades.getTamanoMaximoBytes();
    }

    private static String keyPendiente(UUID carga) {
        return RepositorioArtefactosAlmacenamiento.PREFIJO_PENDIENTES + carga;
    }

    private static UUID uuidODefecto(String texto) {
        return UtilObjeto.aplicarPorDefecto(UtilUUID.generarUUIDDesdeTexto(texto), UtilUUID.obtenerUUIDPorDefecto());
    }

    private static String decodificar(String texto) {
        return UtilObjeto.esNulo(texto) ? UtilTexto.VACIO : URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }
}
