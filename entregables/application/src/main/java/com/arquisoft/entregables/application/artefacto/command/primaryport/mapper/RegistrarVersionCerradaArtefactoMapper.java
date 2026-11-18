package com.arquisoft.entregables.application.artefacto.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;

public final class RegistrarVersionCerradaArtefactoMapper {

    private RegistrarVersionCerradaArtefactoMapper() {}

    public static RegistroVersionCerradaArtefactoDomain toDomain(RegistrarVersionCerradaArtefactoCommand command) {
        var artefacto = ArtefactoDomain.crear(
                command.artefacto(),
                command.proyectoGrado(),
                command.documento(),
                new ArtefactoDomain.VersionCerrada(
                        command.versionArtefacto(), command.numeroVersion(), command.fechaVersion()),
                new ArtefactoDomain.DatosRepositorio(
                        command.versionRepositorioArtefacto(), command.repositorio(), command.nombreRepositorio()));
        return RegistroVersionCerradaArtefactoDomain.crear(artefacto, command.uriArchivo());
    }
}
