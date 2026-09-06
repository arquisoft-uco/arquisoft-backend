package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Réplica local del usuario en solicitudes: la alimenta el consumer de UsuarioCreadoEvent. */
public enum UsuarioReplicaKey implements ClaveMensaje {

    LOG_USUARIO_CREADO_RECIBIDO("solicitudes.infraestructura.usuarioreplica.log.usuario-creado-recibido", 4),
    LOG_USUARIO_CREADO_IGNORADO_SIN_DATOS(
            "solicitudes.infraestructura.usuarioreplica.log.usuario-creado-ignorado-sin-datos", 1),
    LOG_REPLICA_GUARDADA("solicitudes.aplicacion.usuarioreplica.log.replica-guardada", 1);

    private final String clave;
    private final int parametros;

    UsuarioReplicaKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
