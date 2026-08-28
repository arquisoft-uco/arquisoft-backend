package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstudianteFichaPerfil. */
public enum EstudianteFichaPerfilKey implements ClaveMensaje {

    ERROR_DUPLICADO("fichas.dominio.estudiantefichaperfil.error.duplicado", 1),
    ERROR_LIMITE_EXCEDIDO("fichas.dominio.estudiantefichaperfil.error.limite-excedido", 1),
    ERROR_RELACION_NO_ENCONTRADA("fichas.dominio.estudiantefichaperfil.error.relacion-no-encontrada", 2),
    LOG_ASIGNANDO("fichas.aplicacion.estudiantefichaperfil.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("fichas.aplicacion.estudiantefichaperfil.log.verificacion-asignar", 4),
    LOG_ASIGNADO("fichas.aplicacion.estudiantefichaperfil.log.asignado", 2),
    LOG_REMOVIENDO("fichas.aplicacion.estudiantefichaperfil.log.removiendo", 2),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.estudiantefichaperfil.log.verificacion-remover", 3),
    LOG_REMOVIDO("fichas.aplicacion.estudiantefichaperfil.log.removido", 2),
    LOG_VINCULO_GUARDADO("fichas.infraestructura.estudiantefichaperfil.log.vinculo-guardado", 2),
    LOG_VINCULO_ELIMINADO("fichas.infraestructura.estudiantefichaperfil.log.vinculo-eliminado", 2);

    private final String clave;
    private final int parametros;

    EstudianteFichaPerfilKey(String clave, int parametros) {
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
