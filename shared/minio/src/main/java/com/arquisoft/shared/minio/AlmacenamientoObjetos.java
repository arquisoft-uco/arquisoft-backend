package com.arquisoft.shared.minio;

import com.arquisoft.shared.minio.model.FormularioCarga;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;

import java.util.Optional;

public interface AlmacenamientoObjetos {

    FormularioCarga generarFormularioCarga(SolicitudFormularioCarga solicitud);

    MetadatosObjeto consultarMetadatos(String bucket, String key);

    Optional<LecturaObjeto> abrirLectura(String bucket, String key);

    void copiar(String bucket, String keyOrigen, String keyDestino, String etagEsperado);

    void eliminar(String bucket, String key);
}
