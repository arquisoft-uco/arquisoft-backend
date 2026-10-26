package com.arquisoft.shared.tika;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class TikaInspectorArchivoTest {

    private static final long TAMANO_MAXIMO = 5_242_880L;
    private static final String CONTENT_TYPES = "[Content_Types].xml";

    private static final String TIPO_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String TIPO_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String TIPO_PPTX =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    private final TikaInspectorArchivo inspector = new TikaInspectorArchivo();

    private InspeccionResultado inspeccionar(byte[] contenido) throws IOException {
        var inspeccion = inspector.inspeccionar(new ByteArrayInputStream(contenido), TAMANO_MAXIMO);
        return new InspeccionResultado(inspeccion.tipoDetectado(), inspeccion.hashSha256());
    }

    private record InspeccionResultado(String tipo, String hash) {
    }

    @ParameterizedTest
    @CsvSource({
            "plantilla.pdf,application/pdf",
            "plantilla.docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "plantilla.xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "plantilla.pptx,application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "legacy.doc,application/msword"
    })
    void debeDetectarTipo_porContenido(String archivo, String esperado) throws IOException {
        // Act
        var resultado = inspeccionar(fixture(archivo));

        // Assert
        assertThat(resultado.tipo()).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({
            "plantilla.docm,application/vnd.ms-word.document.macroenabled.12",
            "plantilla.xlsm,application/vnd.ms-excel.sheet.macroenabled.12",
            "plantilla.pptm,application/vnd.ms-powerpoint.presentation.macroenabled.12"
    })
    void debeDetectarMacroEnabled_cuandoDocmXlsmPptm(String archivo, String esperado) throws IOException {
        // Act
        var resultado = inspeccionar(fixture(archivo));

        // Assert
        assertThat(resultado.tipo()).isEqualTo(esperado);
    }

    @Test
    void debeDetectarPng_cuandoEsPng() throws IOException {
        // Arrange
        var png = new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A,
                0, 0, 0, 0x0D, 'I', 'H', 'D', 'R', 0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0};

        // Act
        var resultado = inspeccionar(png);

        // Assert
        assertThat(resultado.tipo()).isEqualTo("image/png");
    }

    @Test
    void debeDetectarJpeg_cuandoEsJpeg() throws IOException {
        // Arrange
        var jpeg = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10,
                'J', 'F', 'I', 'F', 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, (byte) 0xFF, (byte) 0xD9};

        // Act
        var resultado = inspeccionar(jpeg);

        // Assert
        assertThat(resultado.tipo()).isEqualTo("image/jpeg");
    }

    @Test
    void debeDetectarTextoPlano_cuandoEsTexto() throws IOException {
        // Arrange
        var texto = "Notas de la reunion\nSegunda linea\n".getBytes(StandardCharsets.UTF_8);

        // Act
        var resultado = inspeccionar(texto);

        // Assert
        assertThat(resultado.tipo()).isEqualTo("text/plain");
    }

    @Test
    void debeDetectarXml_cuandoDrawioConPrologo() throws IOException {
        // Arrange
        var drawio = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<mxfile host=\"app.diagrams.net\"><diagram id=\"a\" name=\"Pagina-1\">AAAA</diagram></mxfile>")
                .getBytes(StandardCharsets.UTF_8);

        // Act
        var resultado = inspeccionar(drawio);

        // Assert
        assertThat(resultado.tipo()).isIn("application/xml", "text/plain");
    }

    @Test
    void debeDetectarSinPrologo_cuandoDrawioEmpiezaEnMxfile() throws IOException {
        // Arrange
        var drawio = "<mxfile host=\"app.diagrams.net\"><diagram id=\"a\" name=\"Pagina-1\">AAAA</diagram></mxfile>"
                .getBytes(StandardCharsets.UTF_8);

        // Act
        var resultado = inspeccionar(drawio);

        // Assert
        assertThat(resultado.tipo()).isIn("application/xml", "text/plain");
    }

    @Test
    void debeCalcularSha256() throws Exception {
        // Arrange
        var bytes = fixture("plantilla.docx");
        var esperado = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));

        // Act
        var resultado = inspeccionar(bytes);

        // Assert
        assertThat(resultado.hash()).isEqualTo(esperado);
    }

    @Test
    void debeCalcularSha256_cuandoDeteccionLeeSoloElPrincipio() throws Exception {
        // Arrange
        var bytes = new byte[200_000];
        var cabecera = "%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(cabecera, 0, bytes, 0, cabecera.length);
        var esperado = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));

        // Act
        var resultado = inspeccionar(bytes);

        // Assert
        assertThat(resultado.hash()).isEqualTo(esperado);
    }

    @Test
    void debeTerminarAcotado_cuandoOoxmlConAltaRazonDeCompresion(@TempDir Path directorio) throws Exception {
        // Arrange
        var bomba = directorio.resolve("bomba.docx");
        var comprimido = armarOoxmlConAltaRazonDeCompresion(bomba);
        assertThat(comprimido).isLessThan(TAMANO_MAXIMO);
        var maximoTemporal = new AtomicLong();
        var activo = new AtomicBoolean(true);
        var vigilante = vigilarTemporales(maximoTemporal, activo);

        // Act
        var inicio = System.nanoTime();
        var resultado = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            try (var flujo = Files.newInputStream(bomba)) {
                return inspector.inspeccionar(flujo, TAMANO_MAXIMO);
            }
        });
        var duracionMs = Duration.ofNanos(System.nanoTime() - inicio).toMillis();
        activo.set(false);
        vigilante.join();

        // Assert
        assertThat(resultado.tipoDetectado()).isIn(TIPO_DOCX, "application/zip", "application/x-tika-ooxml");
        assertThat(duracionMs).isLessThan(5_000L);
        assertThat(maximoTemporal.get()).isLessThanOrEqualTo(comprimido + TAMANO_MAXIMO);
    }

    private static byte[] fixture(String nombre) throws IOException {
        try (var flujo = TikaInspectorArchivoTest.class.getResourceAsStream("/fixtures/archivos/" + nombre)) {
            assertThat(flujo).as("fixture %s", nombre).isNotNull();
            return flujo.readAllBytes();
        }
    }

    private static long armarOoxmlConAltaRazonDeCompresion(Path destino) throws IOException {
        var relleno = new byte[1 << 20];
        Arrays.fill(relleno, (byte) ' ');
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
