package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.Arrays;
import java.util.Locale;

public enum TipoArchivoPlantilla {

    PDF("pdf", "application/pdf", "application/pdf"),
    WORD("docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    EXCEL("xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    POWERPOINT("pptx",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    VACIO(UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);

    private static final char SEPARADOR_EXTENSION = '.';

    private final String extension;
    private final String tipoContenido;
    private final String tipoDetectado;

    TipoArchivoPlantilla(String extension, String tipoContenido, String tipoDetectado) {
        this.extension = extension;
        this.tipoContenido = tipoContenido;
        this.tipoDetectado = tipoDetectado;
    }

    public String getId() {
        return name();
    }

    public String getExtension() {
        return extension;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public String getTipoDetectado() {
        return tipoDetectado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public static TipoArchivoPlantilla desde(String id) {
        return Arrays.stream(values())
                .filter(tipo -> tipo != VACIO && tipo.getId().equals(id))
                .findFirst()
                .orElse(VACIO);
    }

    public static boolean esValido(String id) {
        return !desde(id).esVacio();
    }

    public static TipoArchivoPlantilla desdeExtension(String extension) {
        var normalizada = UtilTexto.aplicarTrim(extension).toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(tipo -> tipo != VACIO && tipo.extension.equals(normalizada))
                .findFirst()
                .orElse(VACIO);
    }

    public static TipoArchivoPlantilla desdeNombreArchivo(String nombreArchivo) {
        var nombre = UtilTexto.aplicarTrim(nombreArchivo);
        var punto = nombre.lastIndexOf(SEPARADOR_EXTENSION);
        return punto < 0 ? VACIO : desdeExtension(nombre.substring(punto + 1));
    }
}
