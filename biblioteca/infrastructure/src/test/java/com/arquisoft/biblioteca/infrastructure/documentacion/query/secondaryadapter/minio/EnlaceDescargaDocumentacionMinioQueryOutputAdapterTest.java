package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.minio;

import com.arquisoft.biblioteca.infrastructure.config.BibliotecaAlmacenamientoProperties;
import com.arquisoft.biblioteca.infrastructure.documentacion.exception.ArchivoDocumentacionAusenteException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class EnlaceDescargaDocumentacionMinioQueryOutputAdapterTest {

    private static final String BUCKET = "entregables";
    private static final String CLAVE = "paquetes/proyecto/paquete-v2.zip";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;


    private EnlaceDescargaDocumentacionMinioQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        var propiedades = new BibliotecaAlmacenamientoProperties();
        propiedades.setBucketEntregables(BUCKET);
        adapter = new EnlaceDescargaDocumentacionMinioQueryOutputAdapter(almacenamientoObjetos, propiedades);
    }

    private void objetoPresente() {
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, CLAVE))
                .thenReturn(new MetadatosObjeto(10L, "application/zip", "etag", Map.of()));
    }

    @Test
    void debeGenerarElEnlaceConElBucketYElNombreDeLaFila_cuandoElObjetoExiste() {
        // Arrange
        var expiraEn = Instant.parse("2026-11-14T12:00:00Z");
        objetoPresente();
        when(almacenamientoObjetos.generarEnlaceDescarga(new SolicitudEnlaceDescarga(BUCKET, CLAVE, "entrega.zip")))
                .thenReturn(new EnlaceDescarga("https://minio.test/firmado", expiraEn));

        // Act
        var enlace = adapter.generar(UtilUUID.generarNuevoUUID(), CLAVE, "entrega.zip");

        // Assert
        assertThat(enlace.url()).isEqualTo("https://minio.test/firmado");
        assertThat(enlace.expiraEn()).isEqualTo(expiraEn);
    }

    @Test
    void debeUsarElUltimoSegmentoDeLaClave_cuandoElNombreDeArchivoNoEstaInformado() {
        // Arrange
        objetoPresente();
        when(almacenamientoObjetos.generarEnlaceDescarga(new SolicitudEnlaceDescarga(BUCKET, CLAVE, "paquete-v2.zip")))
                .thenReturn(new EnlaceDescarga("https://minio.test/firmado", Instant.now()));

        // Act
        var sinNombre = adapter.generar(UtilUUID.generarNuevoUUID(), CLAVE, UtilTexto.VACIO);
        var nombreNulo = adapter.generar(UtilUUID.generarNuevoUUID(), CLAVE, null);

        // Assert
        assertThat(sinNombre.url()).isEqualTo("https://minio.test/firmado");
        assertThat(nombreNulo.url()).isEqualTo("https://minio.test/firmado");
    }

    @Test
    void debeLanzarArchivoAusenteSinFirmar_cuandoLosMetadatosVienenVacios() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.consultarMetadatos(BUCKET, CLAVE)).thenReturn(MetadatosObjeto.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> adapter.generar(documentacion, CLAVE, "entrega.zip"))
                .isInstanceOfSatisfying(ArchivoDocumentacionAusenteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.ARCHIVO_AUSENTE);
                    assertThat(ex.getError().getMessage())
                            .doesNotContain(documentacion.toString())
                            .doesNotContain(CLAVE);
                    assertThat(ex.getMessage()).contains(documentacion.toString(), BUCKET, CLAVE);
                });
        verify(almacenamientoObjetos, never()).generarEnlaceDescarga(any());
    }
}
