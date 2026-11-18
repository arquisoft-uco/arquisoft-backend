package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Autor (réplica local en biblioteca). */
public enum AutorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("biblioteca.infraestructura.autor.log.agregado-recibido", 3),
    LOG_VERIFICACION_AGREGAR("biblioteca.aplicacion.autor.log.verificacion-agregar", 2),
    LOG_AGREGADO("biblioteca.aplicacion.autor.log.agregado", 1),
    LOG_REACTIVADO("biblioteca.aplicacion.autor.log.reactivado", 1),
    LOG_DUPLICADO("biblioteca.aplicacion.autor.log.duplicado", 1),
    LOG_DESCARTADO("biblioteca.aplicacion.autor.log.descartado", 2),
    LOG_GUARDADO("biblioteca.infraestructura.autor.log.guardado", 1),
    LOG_ACTUALIZADO("biblioteca.infraestructura.autor.log.actualizado", 1),
    LOG_REMOVIDO_RECIBIDO("biblioteca.infraestructura.autor.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("biblioteca.aplicacion.autor.log.verificacion-remover", 2),
    LOG_REMOVIDO("biblioteca.aplicacion.autor.log.removido", 1),
    LOG_LAPIDA("biblioteca.aplicacion.autor.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("biblioteca.aplicacion.autor.log.remocion-descartada", 2);

    private final String clave;
    private final int parametros;

    AutorKey(String clave, int parametros) {
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
