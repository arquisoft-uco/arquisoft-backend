package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.minio;

import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.artefactos.infrastructure.versionartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnlaceDescargaVersionArtefactoMinioQueryOutputAdapterTest {

    private static final String BUCKET = "artefactos";
    private static final String KEY = "versiones/abc";
    private static final String RESPALDO = "Documento-v2";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    private EnlaceDescargaVersionArtefactoMinioQueryOutputAdapter adaptador;

    @BeforeEach
    void setUp() {
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        adaptador = new EnlaceDescargaVersionArtefactoMinioQueryOutputAdapter(almacenamientoObjetos, propiedades);
    }

    private static MetadatosObjeto objetoConMetadatos(Map<String, String> metadatos) {
        return new MetadatosObjeto(10L, "application/pdf", "etag", metadatos);
    }

    private SolicitudEnlaceDescarga solicitudEnviada() {
        var captor = ArgumentCaptor.forClass(SolicitudEnlaceDescarga.class);
        verify(almacenamientoObjetos).generarEnlaceDescarga(captor.capture());
        return captor.getValue();
    }

    @Test
    void debePedirEnlaceConBucketDelContextoYUriRelativa_cuandoElObjetoExiste() {
        // Arrange
        var expiraEn = Instant.now();
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY))
                .thenReturn(objetoConMetadatos(Map.of("nombre-archivo", "Guia.pdf")));
        when(almacenamientoObjetos.generarEnlaceDescarga(any()))
                .thenReturn(new EnlaceDescarga("http://minio/firmada", expiraEn));

        // Act
        var resultado = adaptador.generar(UtilUUID.generarNuevoUUID(), KEY, RESPALDO);

        // Assert
        var enviada = solicitudEnviada();
        assertThat(enviada.bucket()).isEqualTo(BUCKET);
        assertThat(enviada.key()).isEqualTo(KEY);
        assertThat(resultado.url()).isEqualTo("http://minio/firmada");
        assertThat(resultado.expiraEn()).isEqualTo(expiraEn);
    }

    @Test
    void debeUsarElMetadatoNombreArchivo_cuandoViene() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY))
                .thenReturn(objetoConMetadatos(Map.of("nombre-archivo", "Guia final.pdf")));
        when(almacenamientoObjetos.generarEnlaceDescarga(any()))
                .thenReturn(new EnlaceDescarga("u", Instant.now()));

        // Act
        adaptador.generar(UtilUUID.generarNuevoUUID(), KEY, RESPALDO);

        // Assert
        assertThat(solicitudEnviada().nombreArchivo()).isEqualTo("Guia final.pdf");
    }

    @Test
    void debeUsarNombreRespaldo_cuandoElMetadatoFalta() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY)).thenReturn(objetoConMetadatos(Map.of()));
        when(almacenamientoObjetos.generarEnlaceDescarga(any()))
                .thenReturn(new EnlaceDescarga("u", Instant.now()));

        // Act
        adaptador.generar(UtilUUID.generarNuevoUUID(), KEY, RESPALDO);

        // Assert
        assertThat(solicitudEnviada().nombreArchivo()).isEqualTo(RESPALDO);
    }

    @Test
    void debeUsarNombreRespaldo_cuandoElMetadatoEstaEnBlanco() {
        // Arrange
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY))
                .thenReturn(objetoConMetadatos(Map.of("nombre-archivo", "   ")));
        when(almacenamientoObjetos.generarEnlaceDescarga(any()))
                .thenReturn(new EnlaceDescarga("u", Instant.now()));

        // Act
        adaptador.generar(UtilUUID.generarNuevoUUID(), KEY, RESPALDO);

        // Assert
        assertThat(solicitudEnviada().nombreArchivo()).isEqualTo(RESPALDO);
    }

    @Test
    void debeLanzarArchivoNoDisponible_cuandoElObjetoNoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY)).thenReturn(MetadatosObjeto.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> adaptador.generar(version, KEY, RESPALDO))
                .isInstanceOfSatisfying(ArchivoVersionNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.VersionArtefacto.ARCHIVO_NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(version.toString(), KEY);
                    assertThat(ex.getError().getDetalle()).contains(version.toString(), KEY);
                });
        verify(almacenamientoObjetos, never()).generarEnlaceDescarga(any());
    }

    @Test
    void debePropagarMinioOperationException_cuandoMinioNoResponde() {
        // Arrange
        var falla = MinioOperationException.consultaFallida(new RuntimeException("caido"));
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, KEY)).thenThrow(falla);

        // Act & Assert
        assertThatThrownBy(() -> adaptador.generar(UtilUUID.generarNuevoUUID(), KEY, RESPALDO)).isSameAs(falla);
        verify(almacenamientoObjetos, never()).generarEnlaceDescarga(any());
    }
}
