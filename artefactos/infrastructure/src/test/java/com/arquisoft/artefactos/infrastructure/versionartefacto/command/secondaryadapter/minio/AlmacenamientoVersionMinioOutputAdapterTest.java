package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.minio;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.FormularioCarga;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlmacenamientoVersionMinioOutputAdapterTest {

    private static final String BUCKET = "artefactos";
    private static final long MAXIMO = 5_242_880L;

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;
    @Mock
    private AppLogger logger;

    private AlmacenamientoVersionMinioOutputAdapter adaptador;

    @BeforeEach
    void setUp() {
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setTamanoMaximoBytes(MAXIMO);
        adaptador = new AlmacenamientoVersionMinioOutputAdapter(almacenamientoObjetos, propiedades, logger);
    }

    @Test
    void debeArmarFormularioConMetadatosYKeyDePendiente() {
        // Arrange
        var solicitud = new SolicitudCargaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Guía final.pdf", "application/pdf", 2048L);
        var expiraEn = Instant.now();
        when(almacenamientoObjetos.generarFormularioCarga(any()))
                .thenReturn(new FormularioCarga("http://minio/bucket", Map.of("k", "v"), expiraEn));

        // Act
        var formulario = adaptador.generarFormularioCarga(solicitud);

        // Assert
        var captor = ArgumentCaptor.forClass(SolicitudFormularioCarga.class);
        verify(almacenamientoObjetos).generarFormularioCarga(captor.capture());
        var enviada = captor.getValue();
        assertThat(enviada.bucket()).isEqualTo(BUCKET);
        assertThat(enviada.key()).isEqualTo("pendientes/" + solicitud.carga());
        assertThat(enviada.tipoContenido()).isEqualTo("application/pdf");
        assertThat(enviada.tamanoMaximoBytes()).isEqualTo(MAXIMO);
        assertThat(enviada.metadatos())
                .containsEntry("solicitante", solicitud.solicitante().toString())
                .containsEntry("artefacto", solicitud.artefacto().toString())
                .containsEntry("nombre-archivo", "Guía final.pdf");
        assertThat(formulario.url()).isEqualTo("http://minio/bucket");
        assertThat(formulario.campos()).containsEntry("k", "v");
        assertThat(formulario.expiraEn()).isEqualTo(expiraEn);
    }

    @Test
    void debeDecodificarNombreArchivoYMapearEtag_cuandoConsultaCarga() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var metadatos = Map.of(
                "solicitante", solicitante.toString(),
                "artefacto", artefacto.toString(),
                "nombre-archivo", "Guía final.pdf");
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, "pendientes/" + carga))
                .thenReturn(new MetadatosObjeto(2048L, "application/pdf", "etag-1", metadatos));

        // Act
        var pendiente = adaptador.obtenerCargaPendiente(carga);

        // Assert
        assertThat(pendiente).isPresent();
        assertThat(pendiente.get().carga()).isEqualTo(carga);
        assertThat(pendiente.get().artefacto()).isEqualTo(artefacto);
        assertThat(pendiente.get().solicitante()).isEqualTo(solicitante);
        assertThat(pendiente.get().nombreArchivo()).isEqualTo("Guía final.pdf");
        assertThat(pendiente.get().tipoContenido()).isEqualTo("application/pdf");
        assertThat(pendiente.get().tamanoBytes()).isEqualTo(2048L);
        assertThat(pendiente.get().etag()).isEqualTo("etag-1");
    }

    @Test
    void debeUsarUuidPorDefecto_cuandoFaltanMetadatos() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, "pendientes/" + carga))
                .thenReturn(new MetadatosObjeto(10L, "application/pdf", "etag", Map.of()));

        // Act
        var pendiente = adaptador.obtenerCargaPendiente(carga).orElseThrow();

        // Assert
        assertThat(UtilUUID.esPorDefecto(pendiente.solicitante())).isTrue();
        assertThat(UtilUUID.esPorDefecto(pendiente.artefacto())).isTrue();
        assertThat(pendiente.nombreArchivo()).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, "pendientes/" + carga))
                .thenReturn(MetadatosObjeto.VACIO);

        // Act & Assert
        assertThat(adaptador.obtenerCargaPendiente(carga)).isEmpty();
    }

    @Test
    void debeCopiarADestino_conEtagEsperado() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();

        // Act
        adaptador.copiarCargaAUri(carga, "etag-1", "versiones/abc");

        // Assert
        verify(almacenamientoObjetos).copiar(BUCKET, "pendientes/" + carga,
                "versiones/abc", "etag-1");
        verify(logger).debug(VersionArtefactoKey.LOG_CARGA_COPIADA, carga, "versiones/abc");
    }

    @Test
    void debeEliminarPendiente() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();

        // Act
        adaptador.eliminarCarga(carga);

        // Assert
        verify(almacenamientoObjetos).eliminar(BUCKET, "pendientes/" + carga);
        verify(logger).debug(VersionArtefactoKey.LOG_CARGA_ELIMINADA, carga);
    }
}
