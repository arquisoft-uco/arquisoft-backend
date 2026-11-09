package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model;

import java.util.List;

public record ArchivoPaqueteArmado(
        String bucket, String claveObjeto, String nombreArchivo, long tamanoBytes, List<EntradaPaqueteArmada> entradas) {}
