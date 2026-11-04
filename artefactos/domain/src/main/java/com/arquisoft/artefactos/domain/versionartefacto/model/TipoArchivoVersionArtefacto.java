package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum TipoArchivoVersionArtefacto {

    PDF(List.of("pdf"), "application/pdf", List.of("application/pdf")),
    WORD(List.of("docx"),
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    EXCEL(List.of("xlsx"),
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            List.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    POWERPOINT(List.of("pptx"),
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            List.of("application/vnd.openxmlformats-officedocument.presentationml.presentation")),
    MARKDOWN(List.of("md"), "text/markdown", List.of("text/plain")),
    TEXTO(List.of("txt"), "text/plain", List.of("text/plain")),
    DRAWIO(List.of("drawio"), "application/vnd.jgraph.mxfile", List.of("application/xml", "text/plain")),
    PNG(List.of("png"), "image/png", List.of("image/png")),
    JPEG(List.of("jpg", "jpeg"), "image/jpeg", List.of("image/jpeg")),

    VACIO(List.of(), UtilTexto.VACIO, List.of());

    private static final char SEPARADOR_EXTENSION = '.';

    private final List<String> extensiones;
    private final String tipoContenido;
    private final List<String> tiposDetectados;

    TipoArchivoVersionArtefacto(List<String> extensiones, String tipoContenido, List<String> tiposDetectados) {
        this.extensiones = extensiones;
        this.tipoContenido = tipoContenido;
        this.tiposDetectados = tiposDetectados;
    }

    public String getId() {
        return name();
    }

    public List<String> getExtensiones() {
        return extensiones;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public List<String> getTiposDetectados() {
        return tiposDetectados;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public static TipoArchivoVersionArtefacto desde(String id) {
        return Arrays.stream(values())
                .filter(tipo -> tipo != VACIO && tipo.getId().equals(id))
                .findFirst()
                .orElse(VACIO);
    }

    public static boolean esValido(String id) {
        return !desde(id).esVacio();
    }

    public static TipoArchivoVersionArtefacto desdeExtension(String extension) {
        var normalizada = UtilTexto.aplicarTrim(extension).toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(tipo -> tipo.extensiones.contains(normalizada))
                .findFirst()
                .orElse(VACIO);
    }

    public static TipoArchivoVersionArtefacto desdeNombreArchivo(String nombreArchivo) {
        var nombre = UtilTexto.aplicarTrim(nombreArchivo);
        var punto = nombre.lastIndexOf(SEPARADOR_EXTENSION);
        return punto < 0 ? VACIO : desdeExtension(nombre.substring(punto + 1));
    }
}
