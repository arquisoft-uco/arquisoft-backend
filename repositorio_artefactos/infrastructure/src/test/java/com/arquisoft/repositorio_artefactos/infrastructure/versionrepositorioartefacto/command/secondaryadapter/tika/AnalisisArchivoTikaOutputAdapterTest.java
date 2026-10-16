package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.tika;

import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosAlmacenamientoProperties;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalisisArchivoTikaOutputAdapterTest {

    private static final String BUCKET = "repositorio-artefactos";
    private static final long TAMANO_MAXIMO = 5_242_880L;
    private static final String ETAG = "etag-leido";
    private static final String CONTENT_TYPES = "[Content_Types].xml";

    private static final String TIPO_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String TIPO_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String TIPO_PPTX =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    private AnalisisArchivoTikaOutputAdapter adaptador;

    @BeforeEach
    void configurar() {
        var propiedades = new RepositorioArtefactosAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        propiedades.setTamanoMaximoBytes(TAMANO_MAXIMO);
        adaptador = new AnalisisArchivoTikaOutputAdapter(almacenamientoObjetos, propiedades);
    }

    @Test
    void debeDetectar_pdf() throws IOException {
        // Arrange
        objeto(fixture("plantilla.pdf"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis).isPresent();
        assertThat(analisis.get().tipoDetectado()).isEqualTo("application/pdf");
    }

    @Test
    void debeDetectar_docx() throws IOException {
        // Arrange
        objeto(fixture("plantilla.docx"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo(TIPO_DOCX);
    }

    @Test
    void debeDetectar_xlsx() throws IOException {
        // Arrange
        objeto(fixture("plantilla.xlsx"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo(TIPO_XLSX);
    }

    @Test
    void debeDetectar_pptx() throws IOException {
        // Arrange
        objeto(fixture("plantilla.pptx"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo(TIPO_PPTX);
    }

    @ParameterizedTest
    @CsvSource({
            "plantilla.docm,application/vnd.ms-word.document.macroenabled.12",
            "plantilla.xlsm,application/vnd.ms-excel.sheet.macroenabled.12",
            "plantilla.pptm,application/vnd.ms-powerpoint.presentation.macroenabled.12"
    })
    void debeDetectarMacroEnabled_cuandoDocmXlsmPptm(String archivo, String esperado) throws IOException {
        // Arrange
        objeto(fixture(archivo));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo(esperado);
    }

    @Test
    void debeDetectarXlsx_aunqueSeaRenombrado() throws IOException {
        // Arrange: el adaptador no recibe nombre alguno, asi que "foto.jpg" no puede influir
        objeto(fixture("plantilla.xlsx"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo(TIPO_XLSX);
    }

    @Test
    void debeDetectarOle2_cuandoDocBinarioRenombradoADocx() throws IOException {
        // Arrange
        objeto(fixture("legacy.doc"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isEqualTo("application/msword");
    }

    @Test
    void debeCalcularSha256() throws Exception {
        // Arrange
        var bytes = fixture("plantilla.docx");
        objeto(bytes);
        var esperado = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().hashSha256()).isEqualTo(esperado);
    }

    @Test
    void debeCalcularSha256_cuandoDeteccionLeeSoloElPrincipio() throws Exception {
        // Arrange: un PDF largo, del que Tika solo lee la firma
        var bytes = new byte[200_000];
        var cabecera = "%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(cabecera, 0, bytes, 0, cabecera.length);
        objeto(bytes);
        var esperado = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().hashSha256()).isEqualTo(esperado);
    }

    @Test
    void debeDevolverEtagDeLaLectura() throws IOException {
        // Arrange
        objeto(fixture("plantilla.pdf"));

        // Act
        var analisis = adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(analisis.orElseThrow().etag()).isEqualTo(ETAG);
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
        var flujo = new ByteArrayInputStream(fixture("plantilla.pdf")) {
            @Override
            public void close() throws IOException {
                cerrado.set(true);
                super.close();
            }
        };
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString()))
                .thenReturn(Optional.of(new LecturaObjeto(flujo, ETAG)));

        // Act
        adaptador.analizarCarga(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(cerrado).isTrue();
    }

    @Test
    void debeTerminarAcotado_cuandoOoxmlConAltaRazonDeCompresion(@TempDir Path directorio) throws Exception {
        // Arrange
        var bomba = directorio.resolve("bomba.docx");
        var comprimido = armarOoxmlConAltaRazonDeCompresion(bomba);
        assertThat(comprimido).isLessThan(TAMANO_MAXIMO);
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString())).thenAnswer(invocacion ->
                Optional.of(new LecturaObjeto(Files.newInputStream(bomba), ETAG)));
        var maximoTemporal = new AtomicLong();
        var activo = new AtomicBoolean(true);
        var vigilante = vigilarTemporales(maximoTemporal, activo);

        // Act
        var inicio = System.nanoTime();
        var analisis = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> adaptador.analizarCarga(UtilUUID.generarNuevoUUID()));
        var duracionMs = Duration.ofNanos(System.nanoTime() - inicio).toMillis();
        activo.set(false);
        vigilante.join();

        // Assert
        assertThat(analisis.orElseThrow().tipoDetectado()).isIn(TIPO_DOCX, "application/zip", "application/x-tika-ooxml");
        assertThat(duracionMs).isLessThan(5_000L);
        assertThat(maximoTemporal.get()).isLessThanOrEqualTo(comprimido + TAMANO_MAXIMO);
    }

    private void objeto(byte[] contenido) {
        when(almacenamientoObjetos.abrirLectura(anyString(), anyString()))
                .thenReturn(Optional.of(new LecturaObjeto(new ByteArrayInputStream(contenido), ETAG)));
    }

    private static byte[] fixture(String nombre) throws IOException {
        try (var flujo = AnalisisArchivoTikaOutputAdapterTest.class
                .getResourceAsStream("/fixtures/archivos/" + nombre)) {
            assertThat(flujo).as("fixture %s", nombre).isNotNull();
            return flujo.readAllBytes();
        }
    }

    private static long armarOoxmlConAltaRazonDeCompresion(Path destino) throws IOException {
        var relleno = new byte[1 << 20];
        java.util.Arrays.fill(relleno, (byte) ' ');
        var ceros = new byte[1 << 20];
        var bloques = 512;

        try (var origen = new ZipInputStream(new ByteArrayInputStream(fixture("plantilla.docx")));
             var zip = new ZipOutputStream(Files.newOutputStream(destino))) {
            zip.setLevel(Deflater.BEST_COMPRESSION);
            ZipEntry entrada;
            while ((entrada = origen.getNextEntry()) != null) {
                var datos = origen.readAllBytes();
                zip.putNextEntry(new ZipEntry(entrada.getName()));
                if (CONTENT_TYPES.equals(entrada.getName())) {
                    escribirConRelleno(zip, new String(datos, StandardCharsets.UTF_8), relleno, bloques);
                } else {
                    zip.write(datos);
                }
                zip.closeEntry();
            }
            zip.putNextEntry(new ZipEntry("relleno.bin"));
            for (var i = 0; i < bloques; i++) {
                zip.write(ceros);
            }
            zip.closeEntry();
        }
        return Files.size(destino);
    }

    private static void escribirConRelleno(OutputStream zip, String xml, byte[] relleno, int bloques)
            throws IOException {
        var finApertura = xml.indexOf('>', xml.indexOf("<Types")) + 1;
        zip.write(xml.substring(0, finApertura).getBytes(StandardCharsets.UTF_8));
        for (var i = 0; i < bloques; i++) {
            zip.write(relleno);
        }
        zip.write(xml.substring(finApertura).getBytes(StandardCharsets.UTF_8));
    }

    private static Thread vigilarTemporales(AtomicLong maximo, AtomicBoolean activo) {
        var tmp = Path.of(System.getProperty("java.io.tmpdir"));
        var hilo = new Thread(() -> {
            while (activo.get()) {
                try (var archivos = Files.list(tmp)) {
                    archivos.filter(p -> p.getFileName().toString().startsWith("apache-tika"))
                            .forEach(p -> {
                                try {
                                    maximo.accumulateAndGet(Files.size(p), Math::max);
                                } catch (IOException ignorado) {
                                    maximo.get();
                                }
                            });
                    Thread.sleep(5);
                } catch (IOException | InterruptedException e) {
                    return;
                }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
        return hilo;
    }
}
