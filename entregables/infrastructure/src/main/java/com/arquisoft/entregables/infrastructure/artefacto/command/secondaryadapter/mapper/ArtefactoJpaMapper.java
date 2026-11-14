package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

public final class ArtefactoJpaMapper {

    private ArtefactoJpaMapper() {}

    public static ArtefactoJpaEntity toJpaEntity(ArtefactoEntity entity) {
        return ArtefactoJpaEntity.builder()
                .id(entity.id())
                .proyectoGrado(entity.proyectoGrado())
                .versionArtefacto(entity.versionArtefacto())
                .numeroVersion(entity.numeroVersion())
                .fechaVersion(entity.fechaVersion())
                .claveObjeto(entity.claveObjeto())
                .versionRepositorioArtefacto(entity.versionRepositorioArtefacto())
                .nombreDocumento(entity.nombreDocumento())
                .repositorio(entity.repositorio())
                .nombreRepositorio(entity.nombreRepositorio())
                .omitidoEn(entity.omitidoEn())
                .omisionOcurridaEn(entity.omisionOcurridaEn())
                .build();
    }

    public static ArtefactoEntity toEntity(ArtefactoJpaEntity jpa) {
        return new ArtefactoEntity(
                jpa.getId(),
                jpa.getProyectoGrado(),
                UtilObjeto.aplicarPorDefecto(jpa.getVersionArtefacto(), UtilUUID.obtenerUUIDPorDefecto()),
                UtilNumero.obtenerPorDefecto(jpa.getNumeroVersion()),
                UtilObjeto.aplicarPorDefecto(jpa.getFechaVersion(), UtilFecha.VACIO),
                UtilObjeto.aplicarPorDefecto(jpa.getClaveObjeto(), UtilTexto.VACIO),
                UtilObjeto.aplicarPorDefecto(jpa.getVersionRepositorioArtefacto(), UtilUUID.obtenerUUIDPorDefecto()),
                UtilObjeto.aplicarPorDefecto(jpa.getNombreDocumento(), UtilTexto.VACIO),
                UtilObjeto.aplicarPorDefecto(jpa.getRepositorio(), UtilUUID.obtenerUUIDPorDefecto()),
                UtilObjeto.aplicarPorDefecto(jpa.getNombreRepositorio(), UtilTexto.VACIO),
                jpa.getOmitidoEn(),
                jpa.getOmisionOcurridaEn());
    }
}
