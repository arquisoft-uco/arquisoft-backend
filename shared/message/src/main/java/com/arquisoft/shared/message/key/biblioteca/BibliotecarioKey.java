package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Bibliotecario (réplica local en biblioteca). */
public enum BibliotecarioKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("biblioteca.infraestructura.bibliotecario.log.agregado-recibido", 3),
    LOG_VERIFICACION_AGREGAR("biblioteca.aplicacion.bibliotecario.log.verificacion-agregar", 2),
    LOG_AGREGADO("biblioteca.aplicacion.bibliotecario.log.agregado", 1),
    LOG_REACTIVADO("biblioteca.aplicacion.bibliotecario.log.reactivado", 1),
    LOG_DUPLICADO("biblioteca.aplicacion.bibliotecario.log.duplicado", 1),
    LOG_DESCARTADO("biblioteca.aplicacion.bibliotecario.log.descartado", 2),
    LOG_GUARDADO("biblioteca.infraestructura.bibliotecario.log.guardado", 1),
    LOG_ACTUALIZADO("biblioteca.infraestructura.bibliotecario.log.actualizado", 1),
    LOG_REMOVIDO_RECIBIDO("biblioteca.infraestructura.bibliotecario.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("biblioteca.aplicacion.bibliotecario.log.verificacion-remover", 2),
    LOG_REMOVIDO("biblioteca.aplicacion.bibliotecario.log.removido", 1),
    LOG_LAPIDA("biblioteca.aplicacion.bibliotecario.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("biblioteca.aplicacion.bibliotecario.log.remocion-descartada", 2);

    private final String clave;
    private final int parametros;

    BibliotecarioKey(String clave, int parametros) {
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
