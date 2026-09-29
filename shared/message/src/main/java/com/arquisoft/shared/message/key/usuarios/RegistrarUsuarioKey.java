package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de catálogo del registro de usuario. */
public enum RegistrarUsuarioKey implements ClaveMensaje {

    ERROR_IDENTIFICADOR_DUPLICADO("usuarios.dominio.usuario.error.identificador-duplicado", 1),
    ERROR_EMAIL_DUPLICADO("usuarios.dominio.usuario.error.email-duplicado", 1),
    ERROR_CONTACTO_DUPLICADO("usuarios.dominio.usuario.error.contacto-duplicado", 1),
    ERROR_NOMBRE_FORMATO("usuarios.dominio.usuario.error.nombre-formato", 1),
    ERROR_CONTACTO_FORMATO("usuarios.dominio.usuario.error.contacto-formato", 1),
    ERROR_ESTADO_NO_ENCONTRADO("usuarios.dominio.estadousuario.error.no-encontrado", 1),
    ERROR_ROL_NO_VALIDO("usuarios.aplicacion.usuario.error.rol-no-valido", 1),
    ERROR_ROL_REQUERIDO("usuarios.aplicacion.usuario.error.rol-requerido", 0),
    LOG_REGISTRANDO("usuarios.aplicacion.usuario.log.registrando", 0),
    LOG_VERIFICACION_REGISTRAR("usuarios.aplicacion.usuario.log.verificacion-registrar", 3),
    LOG_REGISTRADO("usuarios.aplicacion.usuario.log.registrado", 2),
    LOG_USUARIO_GUARDADO("usuarios.infraestructura.usuario.log.guardado", 1),
    LOG_IDP_REGISTRANDO("usuarios.infraestructura.usuario.log.idp-registrando", 1),
    LOG_IDP_ROLES_ASIGNADOS("usuarios.infraestructura.usuario.log.idp-roles-asignados", 2),
    LOG_IDP_ACCIONES_ENVIADAS("usuarios.infraestructura.usuario.log.idp-acciones-enviadas", 1),
    LOG_IDP_ERROR("usuarios.infraestructura.usuario.log.idp-error", 2),
    LOG_IDP_IDENTIDAD_INESPERADA("usuarios.infraestructura.usuario.log.idp-identidad-inesperada", 2),
    LOG_IDP_COMPENSACION_FALLIDA("usuarios.infraestructura.usuario.log.idp-compensacion-fallida", 1),
    LOG_REST_TEMPLATE_CONFIGURADO("usuarios.infraestructura.usuario.log.rest-template-configurado", 2);

    private final String clave;
    private final int parametros;

    RegistrarUsuarioKey(String clave, int parametros) {
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
