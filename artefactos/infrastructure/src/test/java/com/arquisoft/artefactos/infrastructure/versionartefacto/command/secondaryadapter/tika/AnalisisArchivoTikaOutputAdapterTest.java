package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.tika;

import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.tika.InspectorArchivo;
import com.arquisoft.shared.tika.model.InspeccionArchivo;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalisisArchivoTikaOutputAdapterTest {

    private static final String BUCKET = "artefactos";
    private static final long MAXIMO = 5_242_880L;
    private static final String ETAG = "etag-leido";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;
    @Mock
    private InspectorArchivo inspectorArchivo;

    private AnalisisArchivoTikaOutputAdapter adaptador;

    @BeforeEach
    void setUp() {
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setTamanoMaximoBytes(MAXIMO);
        adaptador = new AnalisisArchivoTikaOutputAdapter(almacenamientoObjetos, propiedades, inspectorArchivo);
    }

    @Test
    void debeArmarAnalisisConEtagDeLaLectura() throws IOException {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.abrirLectura(BUCKET, "pendientes/" + carga))
                .thenReturn(Optional.of(new LecturaObjeto(new ByteArrayInputStream(new byte[] {1}), ETAG)));
        when(inspectorArchivo.inspeccionar(any(), eq(MAXIMO)))
                .thenReturn(new InspeccionArchivo("application/pdf", "hash"));

        // Act
        var analisis = adaptador.analizarCarga(carga);

        // Assert
        assertThat(analisis).isPresent();
        assertThat(analisis.get().tipoDetectado()).isEqualTo("application/pdf");
        assertThat(analisis.get().etag()).isEqualTo(ETAG);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayObjeto() throws IOException {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.abrirLectura(BUCKET, "pendientes/" + carga))
                .thenReturn(Optional.empty());

        // Act
        var analisis = adaptador.analizarCarga(carga);

        // Assert
        assertThat(analisis).isEmpty();
        verify(inspectorArchivo, never()).inspeccionar(any(), anyLong());
    }

    @Test
    void debeLanzarMinioOperation_cuandoInspectorFallaConIOException() throws IOException {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamientoObjetos.abrirLectura(BUCKET, "pendientes/" + carga))
                .thenReturn(Optional.of(new LecturaObjeto(new ByteArrayInputStream(new byte[] {1}), ETAG)));
        when(inspectorArchivo.inspeccionar(any(), anyLong())).thenThrow(new IOException("fallo"));

        // Act & Assert
        assertThatThrownBy(() -> adaptador.analizarCarga(carga)).isInstanceOf(MinioOperationException.class);
    }
}
