package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Espejo local del usuario en fichas: lo alimenta el consumer de UsuarioCreadoEvent. */
public enum UsuarioEspejoKey implements ClaveMensaje {

    LOG_USUARIO_CREADO_RECIBIDO("fichas.infraestructura.usuario.log.usuario-creado-recibido", 3),
    LOG_REGISTRADO_ESPEJO_SIMULADO("fichas.infraestructura.usuario.log.registrado-espejo-simulado", 3);

    private final String clave;
    private final int parametros;

    UsuarioEspejoKey(String clave, int parametros) {
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
