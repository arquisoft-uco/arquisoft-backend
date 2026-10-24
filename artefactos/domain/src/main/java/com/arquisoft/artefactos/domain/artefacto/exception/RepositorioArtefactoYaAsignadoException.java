package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.List;
import java.util.UUID;

public final class RepositorioArtefactoYaAsignadoException extends DomainException {

    public RepositorioArtefactoYaAsignadoException(List<UUID> repositorios) {
        super(
                Mensajes.formatear(ArtefactoKey.ERROR_REPOSITORIO_YA_ASIGNADO, repositorios),
                ArtefactosCodes.Artefacto.REPOSITORIO_YA_ASIGNADO
        );
    }
}
