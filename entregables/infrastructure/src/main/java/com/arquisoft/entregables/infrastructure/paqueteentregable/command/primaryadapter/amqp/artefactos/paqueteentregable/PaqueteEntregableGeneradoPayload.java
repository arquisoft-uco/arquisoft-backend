package com.arquisoft.entregables.infrastructure.paqueteentregable.command.primaryadapter.amqp.artefactos.paqueteentregable;

import java.time.Instant;
import java.util.List;

public record PaqueteEntregableGeneradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String paqueteEntregable,
        String entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo,
        Long tamanoBytes,
        Instant generadoEn,
        List<ArtefactoPaqueteGeneradoPayload> artefactos
) {

    public record ArtefactoPaqueteGeneradoPayload(
            String artefacto,
            String versionArtefacto,
            Integer numeroVersion,
            String versionRepositorioArtefacto,
            String nombreDocumento,
            String repositorio,
            String nombreRepositorio,
            String rutaEnPaquete
    ) {
    }
}
