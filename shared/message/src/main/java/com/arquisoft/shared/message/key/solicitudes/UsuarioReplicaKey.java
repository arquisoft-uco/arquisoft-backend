package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/**
 * Réplica local del usuario en solicitudes: la alimentan {@code CoordinadorAgregadoConsumer} y
 * {@code AsesorFichaAgregadoConsumer}, uno por rol publicado por `usuarios`.
 */
public enum UsuarioReplicaKey implements ClaveMensaje {

    LOG_USUARIO_AGREGADO_RECIBIDO("solicitudes.infraestructura.usuarioreplica.log.usuario-agregado-recibido", 4),
    LOG_VERIFICACION_AGREGAR("solicitudes.aplicacion.usuarioreplica.log.verificacion-agregar", 2),
    LOG_AGREGADO("solicitudes.infraestructura.usuarioreplica.log.agregado", 1),
    LOG_DUPLICADO("solicitudes.infraestructura.usuarioreplica.log.duplicado", 1),
    LOG_DESCARTADO("solicitudes.infraestructura.usuarioreplica.log.descartado", 2),
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
