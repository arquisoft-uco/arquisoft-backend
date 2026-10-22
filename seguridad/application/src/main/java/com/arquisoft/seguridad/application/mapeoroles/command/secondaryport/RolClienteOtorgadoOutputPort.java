package com.arquisoft.seguridad.application.mapeoroles.command.secondaryport;

import java.util.Collection;

public interface RolClienteOtorgadoOutputPort {

    boolean rolClienteOtorgadoPor(String rolCliente, Collection<String> rolesRealm);
}
