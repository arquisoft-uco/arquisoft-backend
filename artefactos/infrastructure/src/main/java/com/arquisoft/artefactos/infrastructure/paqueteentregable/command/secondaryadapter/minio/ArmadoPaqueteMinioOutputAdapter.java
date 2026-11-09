package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.minio;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoExcluidoEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoIncluidoEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosAlmacenamientoProperties;
import com.arquisoft.artefactos.infrastructure.versionartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.ArtefactosAlmacenamiento;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.exception.MinioOperationException;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RequiredArgsConstructor
public class ArmadoPaqueteMinioOutputAdapter implements AlmacenamientoPaqueteOutputPort {

    private static final String METADATO_NOMBRE_ARCHIVO = "nombre-archivo";
    private static final String EXTENSION_ZIP = ".zip";
    private static final String TIPO_CONTENIDO_ZIP = "application/zip";
    private static final String PREFIJO_TEMPORAL = "paquete-entregable-";
    private static final String NOMBRE_MANIFIESTO = "manifiesto.json";
    private static final String SIN_NOMBRE = "sin-nombre";
    private static final String SEPARADOR_RUTA = "/";
    private static final String REEMPLAZO = "_";
    private static final int NOMBRE_PROYECTO_MAX = 100;
    private static final String PREFIJO_OOXML = "application/vnd.openxmlformats-officedocument.";
    private static final Set<String> TIPOS_YA_COMPRIMIDOS = Set.of("application/pdf", "image/png", "image/jpeg");
    private static final Pattern CARACTERES_PROHIBIDOS = Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}]");
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private final AlmacenamientoObjetos almacenamientoObjetos;
    private final ArtefactosAlmacenamientoProperties propiedades;
    private final AppLogger logger;

    @Override
    public ArchivoPaqueteArmado armar(ContenidoPaqueteEntity contenido) {
        var bucket = propiedades.getBucket();
        var clave = ArtefactosAlmacenamiento.PREFIJO_PAQUETES + contenido.entregableProyectoGrado() + EXTENSION_ZIP;
        var entradas = prepararEntradas(bucket, contenido);
        var temporal = crearTemporal();
        try {
            escribirZip(bucket, temporal, contenido, entradas);
            var tamano = tamanoDe(temporal);
            subir(bucket, clave, temporal, tamano);
            logger.debug(PaqueteEntregableKey.LOG_ARCHIVO_SUBIDO, bucket, clave, tamano);
            return new ArchivoPaqueteArmado(bucket, clave, nombreArchivo(contenido), tamano);
        } finally {
            borrar(temporal);
        }
    }

    private List<EntradaPaquete> prepararEntradas(String bucket, ContenidoPaqueteEntity contenido) {
        var entradas = new ArrayList<EntradaPaquete>();
        for (var incluido : contenido.incluidos()) {
            var metadatos = almacenamientoObjetos.consultarMetadatos(bucket, incluido.uriArchivo());
            if (metadatos.esVacio()) {
                throw new ArchivoVersionNoDisponibleException(incluido.versionArtefacto(), incluido.uriArchivo());
            }
            var nombreSubido = metadatos.metadatos().get(METADATO_NOMBRE_ARCHIVO);
            var nombreArchivo = UtilTexto.esVacioONulo(nombreSubido)
                    ? ultimoSegmento(incluido.uriArchivo()) : nombreSubido;
            var ruta = String.format(Locale.ROOT, "%02d-%s/v%d-%s", entradas.size() + 1,
                    sanear(incluido.nombreDocumento()), incluido.numeroVersion(), sanear(nombreArchivo));
            entradas.add(new EntradaPaquete(incluido, ruta, esYaComprimido(metadatos.tipoContenido())));
        }
        return entradas;
    }

    private void escribirZip(String bucket, Path destino, ContenidoPaqueteEntity contenido,
                             List<EntradaPaquete> entradas) {
        try (var salida = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(destino)))) {
            for (var entrada : entradas) {
                escribirArchivo(bucket, salida, entrada);
            }
            escribirManifiesto(salida, contenido, entradas);
        } catch (IOException e) {
            throw MinioOperationException.subidaFallida(e);
        }
    }

    private void escribirArchivo(String bucket, ZipOutputStream salida, EntradaPaquete entrada) throws IOException {
        var incluido = entrada.incluido();
        var lectura = almacenamientoObjetos.abrirLectura(bucket, incluido.uriArchivo());
        if (lectura.isEmpty()) {
            throw new ArchivoVersionNoDisponibleException(incluido.versionArtefacto(), incluido.uriArchivo());
        }
        try (var objeto = lectura.get()) {
            salida.setLevel(entrada.yaComprimido() ? Deflater.NO_COMPRESSION : Deflater.DEFAULT_COMPRESSION);
            salida.putNextEntry(new ZipEntry(entrada.ruta()));
            objeto.contenido().transferTo(salida);
            salida.closeEntry();
        }
    }

    private void escribirManifiesto(ZipOutputStream salida, ContenidoPaqueteEntity contenido,
                                    List<EntradaPaquete> entradas) throws IOException {
        var manifiesto = new Manifiesto(
                new EntregableManifiesto(contenido.versionEntregable()),
                new ProyectoGradoManifiesto(contenido.nombreProyecto()),
                contenido.generadoEn().toString(),
                entradas.stream().map(ArmadoPaqueteMinioOutputAdapter::toIncluidoManifiesto).toList(),
                contenido.excluidos().stream().map(ArmadoPaqueteMinioOutputAdapter::toExcluidoManifiesto).toList());
        salida.setLevel(Deflater.DEFAULT_COMPRESSION);
        salida.putNextEntry(new ZipEntry(NOMBRE_MANIFIESTO));
        salida.write(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(manifiesto));
        salida.closeEntry();
    }

    private void subir(String bucket, String clave, Path archivo, long tamano) {
        try (var contenido = new BufferedInputStream(Files.newInputStream(archivo))) {
            almacenamientoObjetos.subir(bucket, clave, contenido, tamano, TIPO_CONTENIDO_ZIP);
        } catch (IOException e) {
            throw MinioOperationException.subidaFallida(e);
        }
    }

    private static Path crearTemporal() {
        try {
            return Files.createTempFile(PREFIJO_TEMPORAL, EXTENSION_ZIP);
        } catch (IOException e) {
            throw MinioOperationException.subidaFallida(e);
        }
    }

    private static long tamanoDe(Path archivo) {
        try {
            return Files.size(archivo);
        } catch (IOException e) {
            throw MinioOperationException.subidaFallida(e);
        }
    }

    private static void borrar(Path archivo) {
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            archivo.toFile().deleteOnExit();
        }
    }

    private static boolean esYaComprimido(String tipoContenido) {
        var tipo = UtilObjeto.esNulo(tipoContenido) ? UtilTexto.VACIO : tipoContenido.toLowerCase(Locale.ROOT);
        return TIPOS_YA_COMPRIMIDOS.contains(tipo) || tipo.startsWith(PREFIJO_OOXML);
    }

    private static String nombreArchivo(ContenidoPaqueteEntity contenido) {
        var proyecto = sanear(contenido.nombreProyecto());
        var recortado = proyecto.length() > NOMBRE_PROYECTO_MAX ? proyecto.substring(0, NOMBRE_PROYECTO_MAX) : proyecto;
        return recortado + "-entregable-v" + contenido.versionEntregable() + EXTENSION_ZIP;
    }

    private static String sanear(String nombre) {
        var saneado = UtilTexto.aplicarTrim(CARACTERES_PROHIBIDOS.matcher(
                UtilObjeto.esNulo(nombre) ? UtilTexto.VACIO : nombre).replaceAll(REEMPLAZO));
        return UtilTexto.esVacioONulo(saneado) ? SIN_NOMBRE : saneado;
    }

    private static String ultimoSegmento(String ruta) {
        return ruta.substring(ruta.lastIndexOf(SEPARADOR_RUTA) + 1);
    }

    private static IncluidoManifiesto toIncluidoManifiesto(EntradaPaquete entrada) {
        var incluido = entrada.incluido();
        return new IncluidoManifiesto(
                incluido.artefacto(), incluido.nombreDocumento(), incluido.numeroVersion(),
                incluido.ultimaVersion(), incluido.fechaVersion().toString(), entrada.ruta());
    }

    private static ExcluidoManifiesto toExcluidoManifiesto(ArtefactoExcluidoEntity excluido) {
        return new ExcluidoManifiesto(
                excluido.artefacto(), excluido.nombreDocumento(), excluido.motivo(), excluido.ultimaVersion());
    }

    record EntradaPaquete(ArtefactoIncluidoEntity incluido, String ruta, boolean yaComprimido) {}

    record Manifiesto(
            EntregableManifiesto entregable,
            ProyectoGradoManifiesto proyectoGrado,
            String generadoEn,
            List<IncluidoManifiesto> incluidos,
            List<ExcluidoManifiesto> excluidos) {}

    record EntregableManifiesto(int version) {}

    record ProyectoGradoManifiesto(String nombre) {}

    record IncluidoManifiesto(
            UUID artefacto, String documento, int version, int ultimaVersion, String fechaVersion, String archivo) {}

    record ExcluidoManifiesto(UUID artefacto, String documento, String motivo, int ultimaVersion) {}
}
