package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.tika;

import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalisisArchivoTikaOutputAdapterTest {

    private static final String BUCKET = "repositorio-artefactos";
    private static final long TAMANO_MAXIMO = 5_242_880L;
    private static final String ETAG = "etag-leido";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;
    @Mock
    private InspectorArchivo inspectorArchivo;

    private AnalisisArchivoTikaOutputAdapter adaptador;

    @BeforeEach
    void configurar() {
        var propiedades = new RepositorioArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setTamanoMaximoBytes(TAMANO_MAXIMO);
        adaptador = new AnalisisArchivoTikaOutputAdapter(almacenamientoObjetos, propiedades, inspectorArchivo);
    }

    @Test
    void debeArmarAnalisisConEtagDeLaLectura_cuandoHayObjeto() throws IOException {
        // Arrange
        var hash = "a".repeat(64);
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString()))
                .thenReturn(Optional.of(new LecturaObjeto(new ByteArrayInputStream(new byte[] {1}), ETAG)));
        when(inspectorArchivo.inspeccionar(any(), anyLong()))
                .thenReturn(new InspeccionArchivo("application/pdf", hash));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis).isPresent();
        assertThat(analisis.get().tipoDetectado()).isEqualTo("application/pdf");
        assertThat(analisis.get().hashSha256()).isEqualTo(hash);
        assertThat(analisis.get().etag()).isEqualTo(ETAG);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayObjeto() {
        // Arrange
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis).isEmpty();
    }

    @Test
    void debeCerrarElFlujo() throws IOException {
        // Arrange
        var cerrado = new AtomicBoolean(false);
        var flujo = new ByteArrayInputStream(new byte[] {1}) {
            @Override
            public void close() throws IOException {
                cerrado.set(true);
                super.close();
            }
        };
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString()))
                .thenReturn(Optional.of(new LecturaObjeto(flujo, ETAG)));
        when(inspectorArchivo.inspeccionar(any(), anyLong()))
                .thenReturn(new InspeccionArchivo("application/pdf", "h"));

        // Act
        adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(cerrado).isTrue();
    }

    @Test
    void debeLanzarMinioOperation_cuandoInspectorFallaConIOException() throws IOException {
        // Arrange
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString()))
                .thenReturn(Optional.of(new LecturaObjeto(new ByteArrayInputStream(new byte[] {1}), ETAG)));
        when(inspectorArchivo.inspeccionar(any(), anyLong())).thenThrow(new IOException("fallo"));
        var carga = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> adaptador.analizarCarga(carga)).isInstanceOf(MinioOperationException.class);
    }
}
