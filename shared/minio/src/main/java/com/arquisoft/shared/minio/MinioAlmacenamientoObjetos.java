package com.arquisoft.shared.minio;

import com.arquisoft.shared.minio.config.MinioProperties;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.FormularioCarga;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import com.arquisoft.shared.util.UtilObjeto;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MinioAlmacenamientoObjetos implements AlmacenamientoObjetos {

    private static final String CODIGO_OBJETO_INEXISTENTE = "NoSuchKey";
    private static final String CAMPO_KEY = "key";
    private static final String CAMPO_TIPO_CONTENIDO = "Content-Type";
    private static final String PREFIJO_METADATO = "x-amz-meta-";
    private static final String CABECERA_ETAG = "ETag";
    private static final String COMILLAS = "\"";
    private static final String SEPARADOR_RUTA = "/";
    private static final String SIN_TEXTO = "";
    private static final long TAMANO_MINIMO_BYTES = 1L;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public FormularioCarga generarFormularioCarga(SolicitudFormularioCarga solicitud) {
        try {
            var expiraEn = ZonedDateTime.now(ZoneOffset.UTC)
                    .plusMinutes(properties.getPresignedUrlExpiry().getFormularioCargaMinutos());
            var politica = new PostPolicy(solicitud.bucket(), expiraEn);
            var camposExactos = new LinkedHashMap<String, String>();
            camposExactos.put(CAMPO_KEY, solicitud.key());
            camposExactos.put(CAMPO_TIPO_CONTENIDO, solicitud.tipoContenido());
            solicitud.metadatos().forEach((nombre, valor) ->
                    camposExactos.put(PREFIJO_METADATO + nombre, URLEncoder.encode(valor, StandardCharsets.UTF_8)));

            camposExactos.forEach(politica::addEqualsCondition);
            politica.addContentLengthRangeCondition(TAMANO_MINIMO_BYTES, solicitud.tamanoMaximoBytes());

            var campos = new LinkedHashMap<String, String>(minioClient.getPresignedPostFormData(politica));
            campos.putAll(camposExactos);
            return new FormularioCarga(urlPublica(solicitud.bucket()), campos, expiraEn.toInstant());
        } catch (Exception e) {
            throw fallo(MinioOperationException::formularioCargaFallido, e);
        }
    }

    @Override
    public MetadatosObjeto consultarMetadatos(String bucket, String key) {
        try {
            var respuesta = minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(key).build());
            return new MetadatosObjeto(
                    respuesta.size(),
                    respuesta.contentType(),
                    normalizarEtag(respuesta.etag()),
                    normalizarMetadatos(respuesta.userMetadata()));
        } catch (ErrorResponseException e) {
            if (esObjetoInexistente(e)) {
                return MetadatosObjeto.VACIO;
            }
            throw fallo(MinioOperationException::consultaFallida, e);
        } catch (Exception e) {
            throw fallo(MinioOperationException::consultaFallida, e);
        }
    }

    @Override
    public Optional<LecturaObjeto> abrirLectura(String bucket, String key) {
        try {
            var respuesta = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build());
            return Optional.of(new LecturaObjeto(respuesta, normalizarEtag(respuesta.headers().get(CABECERA_ETAG))));
        } catch (ErrorResponseException e) {
            if (esObjetoInexistente(e)) {
                return Optional.empty();
            }
            throw fallo(MinioOperationException::lecturaFallida, e);
        } catch (Exception e) {
            throw fallo(MinioOperationException::lecturaFallida, e);
        }
    }

    @Override
    public void copiar(String bucket, String keyOrigen, String keyDestino, String etagEsperado) {
        try {
            var origen = CopySource.builder()
                    .bucket(bucket)
                    .object(keyOrigen)
                    .matchETag(normalizarEtag(etagEsperado))
                    .build();
            minioClient.copyObject(CopyObjectArgs.builder().bucket(bucket).object(keyDestino).source(origen).build());
        } catch (Exception e) {
            throw fallo(MinioOperationException::copiaFallida, e);
        }
    }

    @Override
    public void eliminar(String bucket, String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception e) {
            throw fallo(MinioOperationException::eliminacionFallida, e);
        }
    }

    private String urlPublica(String bucket) {
        var endpoint = properties.getEndpointPublico();
        var base = endpoint.endsWith(SEPARADOR_RUTA) ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + SEPARADOR_RUTA + bucket;
    }

    private static boolean esObjetoInexistente(ErrorResponseException e) {
        return CODIGO_OBJETO_INEXISTENTE.equals(e.errorResponse().code());
    }

    private static String normalizarEtag(String etag) {
        return UtilObjeto.esNulo(etag) ? SIN_TEXTO : etag.replace(COMILLAS, SIN_TEXTO);
    }

    private static Map<String, String> normalizarMetadatos(Map<String, String> metadatos) {
        var normalizados = new LinkedHashMap<String, String>();
        metadatos.forEach((nombre, valor) -> {
            var minuscula = nombre.toLowerCase(Locale.ROOT);
            normalizados.put(
                    minuscula.startsWith(PREFIJO_METADATO) ? minuscula.substring(PREFIJO_METADATO.length()) : minuscula,
                    URLDecoder.decode(valor, StandardCharsets.UTF_8));
        });
        return normalizados;
    }

    private static MinioOperationException fallo(Function<Exception, MinioOperationException> fabrica, Exception causa) {
        restaurarInterrupcion(causa);
        return fabrica.apply(causa);
    }

    private static void restaurarInterrupcion(Exception causa) {
        if (causa instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
