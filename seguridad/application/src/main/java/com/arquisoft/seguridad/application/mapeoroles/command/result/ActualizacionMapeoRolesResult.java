package com.arquisoft.seguridad.application.mapeoroles.command.result;

public sealed interface ActualizacionMapeoRolesResult {

    record Actualizada(int rolesRealm, int rolesCliente) implements ActualizacionMapeoRolesResult {}

    record Vigente() implements ActualizacionMapeoRolesResult {}

    record Omitida() implements ActualizacionMapeoRolesResult {}

    record Fallida(String motivo) implements ActualizacionMapeoRolesResult {}
}
