package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoExcluidoEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoIncluidoEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoExcluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoIncluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoPaqueteGenerado;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactosDelPaquete;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;

import java.util.List;
import java.util.stream.Collectors;

public final class PaqueteEntregableMapper {

    private PaqueteEntregableMapper() {}

    public static PaqueteEntregableEntity toEntity(PaqueteEntregableDomain paquete) {
        return new PaqueteEntregableEntity(
                paquete.getId(),
                paquete.getEntregableProyectoGrado(),
                paquete.getProyectoGrado(),
                paquete.getVersionEntregable(),
                paquete.getClaveObjeto(),
                paquete.getTamanoBytes(),
                paquete.getGeneradoEn(),
                paquete.getIncluidos().stream()
                        .map(incluido -> new ArtefactoPaqueteEntity(incluido.artefacto(), incluido.numeroVersion()))
                        .toList());
    }

    public static ContenidoPaqueteEntity toContenido(
            GeneracionPaqueteEntregableDomain generacion, ProyectoGradoDomain proyecto, ArtefactosDelPaquete artefactos) {
        return new ContenidoPaqueteEntity(
                generacion.getEntregableProyectoGrado(),
                generacion.getVersionEntregable(),
                proyecto.getNombre(),
                generacion.getGeneradoEn(),
                artefactos.incluidos().stream().map(PaqueteEntregableMapper::toIncluidoEntity).toList(),
                artefactos.excluidos().stream().map(PaqueteEntregableMapper::toExcluidoEntity).toList());
    }

    public static List<ArtefactoPaqueteGenerado> toArtefactosGenerados(
            List<ArtefactoIncluidoPaquete> incluidos, List<EntradaPaqueteArmada> entradas) {
        var rutaPorArtefacto = entradas.stream().collect(
                Collectors.toMap(EntradaPaqueteArmada::artefacto, EntradaPaqueteArmada::rutaEnPaquete));
        return incluidos.stream()
                .map(incluido -> new ArtefactoPaqueteGenerado(
                        incluido.artefacto(),
                        incluido.numeroVersion(),
                        incluido.versionRepositorioArtefacto(),
                        incluido.nombreDocumento(),
                        incluido.repositorio(),
                        incluido.nombreRepositorio(),
                        rutaPorArtefacto.get(incluido.artefacto())))
                .toList();
    }

    private static ArtefactoIncluidoEntity toIncluidoEntity(ArtefactoIncluidoPaquete incluido) {
        return new ArtefactoIncluidoEntity(
                incluido.artefacto(),
                incluido.versionArtefacto(),
                incluido.nombreDocumento(),
                incluido.numeroVersion(),
                incluido.ultimaVersion(),
                incluido.fechaVersion(),
                incluido.uriArchivo());
    }

    private static ArtefactoExcluidoEntity toExcluidoEntity(ArtefactoExcluidoPaquete excluido) {
        return new ArtefactoExcluidoEntity(
                excluido.artefacto(),
                excluido.nombreDocumento(),
                excluido.motivo().name(),
                excluido.ultimaVersion());
    }
}
