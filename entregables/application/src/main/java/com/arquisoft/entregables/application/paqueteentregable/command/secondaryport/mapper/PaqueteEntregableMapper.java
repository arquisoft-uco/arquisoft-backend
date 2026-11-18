package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ArtefactoIncluidoEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

import java.time.Instant;
import java.util.List;

public final class PaqueteEntregableMapper {

    private PaqueteEntregableMapper() {}

    public static PaqueteEntregableEntity toEntity(PaqueteEntregableDomain paquete) {
        return new PaqueteEntregableEntity(
                paquete.getId(),
                paquete.getEntregableProyectoGrado(),
                paquete.getBucket(),
                paquete.getClaveObjeto(),
                paquete.getNombreArchivo(),
                paquete.getTamanoBytes(),
                paquete.getGeneradoEn());
    }

    public static PaqueteEntregableDomain toDomain(PaqueteEntregableEntity entity) {
        return PaqueteEntregableDomain.reconstruir(
                entity.id(),
                entity.entregableProyectoGrado(),
                entity.bucket(),
                entity.claveObjeto(),
                entity.nombreArchivo(),
                entity.tamanoBytes(),
                entity.generadoEn());
    }

    public static ContenidoPaqueteEntity toContenido(
            EntregableProyectoGradoDomain entregable, ProyectoGradoDomain proyecto, List<ArtefactoDomain> artefactos,
            Instant generadoEn) {
        return new ContenidoPaqueteEntity(
                entregable.getId(),
                entregable.getVersion(),
                proyecto.getNombre(),
                generadoEn,
                artefactos.stream()
                        .map(artefacto -> new ArtefactoIncluidoEntity(
                                artefacto.getId(),
                                artefacto.getNombreDocumento(),
                                artefacto.getNumeroVersion(),
                                artefacto.getFechaVersion(),
                                artefacto.getClaveObjeto()))
                        .toList());
    }
}
