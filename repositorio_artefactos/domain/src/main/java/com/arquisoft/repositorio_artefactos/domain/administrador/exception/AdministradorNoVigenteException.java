package com.arquisoft.repositorio_artefactos.domain.administrador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;

import java.util.UUID;

public final class AdministradorNoVigenteException extends DomainException {

    public AdministradorNoVigenteException(UUID administrador) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_NO_VIGENTE,
                Mensajes.obtener(RepositorioArtefactoKey.ERROR_ADMINISTRADOR_NO_VIGENTE),
                Mensajes.formatear(RepositorioArtefactoKey.ERROR_ADMINISTRADOR_NO_VIGENTE_DETALLE, administrador)
        ));
    }
}
