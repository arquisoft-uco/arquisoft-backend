package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.minio;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ArtefactoIncluidoEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.entregables.infrastructure.config.EntregablesAlmacenamientoProperties;
import com.arquisoft.entregables.infrastructure.paqueteentregable.exception.ArchivoArtefactoNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.FormularioCarga;
import com.arquisoft.shared.minio.model.LecturaObjeto;
import com.arquisoft.shared.minio.model.MetadatosObjeto;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import com.arquisoft.shared.minio.model.SolicitudFormularioCarga;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ArmadoPaqueteMinioOutputAdapterTest {

    private static final String BUCKET = "bucket-entregables";
    private static final String PDF = "application/pdf";
    private static final String OOXML = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String MARKDOWN = "text/markdown";
    private static final String PREFIJO_TEMPORAL = "paquete-entregable-";

    private final JsonMapper mapper = JsonMapper.builder().build();
    private final AppLogger logger = mock(AppLogger.class);

    private AlmacenamientoFalso almacenamiento;
    private ArmadoPaqueteMinioOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        almacenamiento = new AlmacenamientoFalso();
        var propiedades = new EntregablesAlmacenamientoProperties();
        propiedades.setBucket(BUCKET);
        adapter = new ArmadoPaqueteMinioOutputAdapter(almacenamiento, propiedades, logger);
    }

    private ArtefactoIncluidoEntity incluir(String documento, String archivo, String tipo, byte[] bytes) {
        var clave = "entregables/artefactos/" + UtilUUID.generarNuevoUUID() + "/v2";
        almacenamiento.objetos.put(clave, new Objeto(bytes, tipo, Map.of("nombre-archivo", archivo)));
        return new ArtefactoIncluidoEntity(UtilUUID.generarNuevoUUID(), documento, 2,
                Instant.parse("2026-03-01T00:00:00Z"), clave);
    }

    private ContenidoPaqueteEntity contenido(UUID entregable, String proyecto, List<ArtefactoIncluidoEntity> incluidos) {
        return new ContenidoPaqueteEntity(entregable, 4, proyecto, Instant.parse("2026-11-08T10:00:00Z"), incluidos);
    }

    private static byte[] repetido(char caracter) {
        return String.valueOf(caracter).repeat(20_000).getBytes(StandardCharsets.UTF_8);
    }

    private ZipFile abrirZip() throws IOException {
        var temporal = Files.createTempFile("verificacion-", ".zip");
        Files.write(temporal, almacenamiento.subido);
        temporal.toFile().deleteOnExit();
        return new ZipFile(temporal.toFile());
    }

    private static List<String> nombresDe(ZipFile zip) {
        return zip.stream().map(entrada -> entrada.getName()).collect(Collectors.toList());
    }

    private static List<Path> temporalesDelPaquete() throws IOException {
        try (var archivos = Files.list(Path.of(System.getProperty("java.io.tmpdir")))) {
            return archivos.filter(ruta -> ruta.getFileName().toString().startsWith(PREFIJO_TEMPORAL)).toList();
        }
    }

    @Test
    void debeSubirZipConEntradasYManifiestoSinExcluidos_cuandoHayIncluidos() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var incluido = incluir("Documento", "informe.pdf", PDF, repetido('a'));

        // Act
        var archivo = adapter.armar(contenido(entregable, "Proyecto X", List.of(incluido)));

        // Assert
        assertThat(archivo.bucket()).isEqualTo(BUCKET);
        assertThat(archivo.claveObjeto()).isEqualTo("entregables/paquetes/" + entregable + ".zip");
        assertThat(archivo.nombreArchivo()).isEqualTo("Proyecto X-entregable-v4.zip");
        assertThat(archivo.tamanoBytes()).isEqualTo(almacenamiento.subido.length);
        assertThat(almacenamiento.bucketSubida).isEqualTo(BUCKET);
        assertThat(almacenamiento.claveSubida).isEqualTo(archivo.claveObjeto());
        assertThat(almacenamiento.tipoSubida).isEqualTo("application/zip");
        try (var zip = abrirZip()) {
            assertThat(nombresDe(zip)).containsExactly("01-Documento/v2-informe.pdf", "manifiesto.json");
            var manifiesto = mapper.readTree(zip.getInputStream(zip.getEntry("manifiesto.json")).readAllBytes());
            assertThat(manifiesto.get("entregable").get("version").asInt()).isEqualTo(4);
            assertThat(manifiesto.get("proyectoGrado").get("nombre").asString()).isEqualTo("Proyecto X");
            assertThat(manifiesto.has("excluidos")).isFalse();
            assertThat(manifiesto.get("incluidos")).hasSize(1);
            assertThat(manifiesto.get("incluidos").get(0).has("ultimaVersion")).isFalse();
            assertThat(manifiesto.get("incluidos").get(0).get("artefacto").asString())
                    .isEqualTo(incluido.artefacto().toString());
            assertThat(manifiesto.get("incluidos").get(0).get("documento").asString()).isEqualTo("Documento");
            assertThat(manifiesto.get("incluidos").get(0).get("version").asInt()).isEqualTo(2);
            assertThat(manifiesto.get("incluidos").get(0).get("archivo").asString())
                    .isEqualTo("01-Documento/v2-informe.pdf");
            assertThat(zip.getInputStream(zip.getEntry("01-Documento/v2-informe.pdf")).readAllBytes())
                    .isEqualTo(repetido('a'));
        }
    }

    @Test
    void debeDejarSinComprimirPdfYOoxml_yComprimirTexto() throws Exception {
        // Arrange
        var pdf = incluir("Pdf", "a.pdf", PDF, repetido('p'));
        var word = incluir("Word", "b.docx", OOXML, repetido('w'));
        var texto = incluir("Texto", "c.md", MARKDOWN, repetido('t'));

        // Act
        adapter.armar(contenido(UtilUUID.generarNuevoUUID(), "Proyecto", List.of(pdf, word, texto)));

        // Assert
        try (var zip = abrirZip()) {
            var entradaPdf = zip.getEntry("01-Pdf/v2-a.pdf");
            var entradaWord = zip.getEntry("02-Word/v2-b.docx");
            var entradaTexto = zip.getEntry("03-Texto/v2-c.md");
            assertThat(entradaPdf.getCompressedSize()).isGreaterThanOrEqualTo(entradaPdf.getSize());
            assertThat(entradaWord.getCompressedSize()).isGreaterThanOrEqualTo(entradaWord.getSize());
            assertThat(entradaTexto.getCompressedSize()).isLessThan(entradaTexto.getSize() / 10);
        }
    }

    @Test
    void debeSanearNumerarYDevolverLaMismaRutaQueElZipYElManifiesto() throws Exception {
        // Arrange
        var primero = incluir("Doc: A/B", "in?forme*.pdf", PDF, repetido('a'));
        var segundo = incluir("Anexo", "anexo.md", MARKDOWN, repetido('b'));

        // Act
        var archivo = adapter.armar(contenido(UtilUUID.generarNuevoUUID(), "Proyecto <Z>", List.of(primero, segundo)));

        // Assert
        assertThat(archivo.entradas()).extracting(EntradaPaqueteArmada::artefacto)
                .containsExactly(primero.artefacto(), segundo.artefacto());
        try (var zip = abrirZip()) {
            assertThat(nombresDe(zip)).containsExactly(
                    "01-Doc_ A_B/v2-in_forme_.pdf", "02-Anexo/v2-anexo.md", "manifiesto.json");
            var manifiesto = mapper.readTree(zip.getInputStream(zip.getEntry("manifiesto.json")).readAllBytes());
            for (var i = 0; i < archivo.entradas().size(); i++) {
                var ruta = archivo.entradas().get(i).rutaEnPaquete();
                assertThat(zip.getEntry(ruta)).isNotNull();
                assertThat(manifiesto.get("incluidos").get(i).get("archivo").asString()).isEqualTo(ruta);
            }
        }
    }

    @Test
    void debeLanzar503ArchivoArtefactoNoDisponible_cuandoLaCopiaNoExiste() {
        // Arrange
        var incluido = new ArtefactoIncluidoEntity(UtilUUID.generarNuevoUUID(), "Doc", 1, Instant.now(),
                "entregables/artefactos/x/v1");
        var contenido = contenido(UtilUUID.generarNuevoUUID(), "Proyecto", List.of(incluido));

        // Act & Assert
        assertThatThrownBy(() -> adapter.armar(contenido))
                .isInstanceOfSatisfying(ArchivoArtefactoNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.ARCHIVO_ARTEFACTO_NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(incluido.artefacto().toString());
                });
        assertThat(almacenamiento.subido).isNull();
    }

    @Test
    void debeBorrarElTemporal_cuandoLaSubidaFalla() throws Exception {
        // Arrange
        almacenamiento.fallarSubida = true;
        var antes = temporalesDelPaquete();
        var contenido = contenido(UtilUUID.generarNuevoUUID(), "Proyecto", List.of());

        // Act & Assert
        assertThatThrownBy(() -> adapter.armar(contenido))
                .isInstanceOfSatisfying(MinioOperationException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(AppCodes.Minio.SUBIDA_FALLIDA));
        var despues = new ArrayList<>(temporalesDelPaquete());
        despues.removeAll(antes);
        assertThat(despues).isEmpty();
    }

    @Test
    void debeEliminarElObjetoEnElBucketIndicado_cuandoSeBorraUnZip() {
        // Arrange
        var clave = "paquetes/" + UtilUUID.generarNuevoUUID() + ".zip";

        // Act
        adapter.eliminar("bucket-viejo", clave);

        // Assert
        assertThat(almacenamiento.eliminado).containsExactly("bucket-viejo/" + clave);
    }

    private record Objeto(byte[] contenido, String tipo, Map<String, String> metadatos) {}

    private static final class AlmacenamientoFalso implements AlmacenamientoObjetos {

        private final Map<String, Objeto> objetos = new HashMap<>();
        private byte[] subido;
        private String bucketSubida;
        private String claveSubida;
        private String tipoSubida;
        private boolean fallarSubida;
        private final List<String> eliminado = new ArrayList<>();

        @Override
        public FormularioCarga generarFormularioCarga(SolicitudFormularioCarga solicitud) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnlaceDescarga generarEnlaceDescarga(SolicitudEnlaceDescarga solicitud) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MetadatosObjeto consultarMetadatos(String bucket, String key) {
            var objeto = objetos.get(key);
            if (objeto == null) {
                return MetadatosObjeto.VACIO;
            }
            return new MetadatosObjeto(objeto.contenido().length, objeto.tipo(), "etag", objeto.metadatos());
        }

        @Override
        public Optional<LecturaObjeto> abrirLectura(String bucket, String key) {
            return Optional.ofNullable(objetos.get(key))
                    .map(objeto -> new LecturaObjeto(new ByteArrayInputStream(objeto.contenido()), "etag"));
        }

        @Override
        public void copiar(String bucket, String keyOrigen, String keyDestino, String etagEsperado) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void subir(String bucket, String key, InputStream contenido, long tamano, String tipoContenido) {
            if (fallarSubida) {
                throw MinioOperationException.subidaFallida(new IOException("sin conexion"));
            }
            try {
                subido = contenido.readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            bucketSubida = bucket;
            claveSubida = key;
            tipoSubida = tipoContenido;
        }

        @Override
        public void eliminar(String bucket, String key) {
            eliminado.add(bucket + "/" + key);
        }
    }
}
