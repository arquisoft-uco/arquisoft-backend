package com.arquisoft.seguridad.application.auth.command.secondaryport;

import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;

import java.util.Optional;

public interface ValidacionTokenOutputPort {

    Optional<IdentidadProveedor> extraerIdentidad(String token);
}
