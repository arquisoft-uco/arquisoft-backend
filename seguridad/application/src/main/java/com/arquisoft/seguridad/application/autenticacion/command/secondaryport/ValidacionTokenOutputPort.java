package com.arquisoft.seguridad.application.autenticacion.command.secondaryport;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.IdentidadProveedor;

import java.util.Optional;

public interface ValidacionTokenOutputPort {

    Optional<IdentidadProveedor> extraerIdentidad(String token);
}
