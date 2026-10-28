package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.List;
import java.util.UUID;

public final class RepositorioArtefactoYaAsignadoException extends DomainException {

    private static final String SEPARADOR_NOMBRES = "», «";

    public RepositorioArtefactoYaAsignadoException(List<UUID> repositorios, List<String> nombresRepositorios) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.REPOSITORIO_YA_ASIGNADO,
                Mensajes.formatear(ArtefactoKey.ERROR_REPOSITORIO_YA_ASIGNADO,
                        String.join(SEPARADOR_NOMBRES, nombresRepositorios)),
                Mensajes.formatear(ArtefactoKey.ERROR_REPOSITORIO_YA_ASIGNADO_DETALLE, repositorios)
        ));
    }
}
