package com.arquisoft.shared.minio;

import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.FormularioCarga;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;

import java.io.InputStream;
import java.util.Optional;

public interface AlmacenamientoObjetos {

    FormularioCarga generarFormularioCarga(SolicitudFormularioCarga solicitud);

    EnlaceDescarga generarEnlaceDescarga(SolicitudEnlaceDescarga solicitud);

    MetadatosObjeto consultarMetadatos(String bucket, String key);

    Optional<LecturaObjeto> abrirLectura(String bucket, String key);

    void copiar(String bucket, String keyOrigen, String keyDestino, String etagEsperado);

    void subir(String bucket, String key, InputStream contenido, long tamano, String tipoContenido);

    void eliminar(String bucket, String key);
}
