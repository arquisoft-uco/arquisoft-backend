package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoFichaPerfil. */
public enum EstadoFichaPerfilKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estadofichaperfil.error.no-encontrado", 1),
    ERROR_ESTADO_TERMINAL("fichas.dominio.estadofichaperfil.error.estado-terminal", 1),
    LOG_CREADO("fichas.aplicacion.estadofichaperfil.log.creado", 3),
    LOG_CONSULTANDO_ESTUDIANTE("fichas.aplicacion.estadofichaperfil.log.consultando-estudiante", 1),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("fichas.aplicacion.estadofichaperfil.log.consulta-estudiante-completada", 1),
    LOG_CONSULTANDO_ASESOR("fichas.aplicacion.estadofichaperfil.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("fichas.aplicacion.estadofichaperfil.log.consulta-asesor-completada", 3),
    LOG_GUARDADO("fichas.infraestructura.estadofichaperfil.log.guardado", 2),
    ERROR_NO_DISPONIBLE_PARA_EVALUACION("fichas.dominio.estadofichaperfil.error.no-disponible-para-evaluacion", 1),
    ERROR_SIN_EVALUACION_FINALIZADA("fichas.dominio.estadofichaperfil.error.sin-evaluacion-finalizada", 1),
    ERROR_APROBACION_SIN_EVALUACION_APROBATORIA(
            "fichas.dominio.estadofichaperfil.error.aprobacion-sin-evaluacion-aprobatoria", 1),
    ERROR_SIN_ESTUDIANTES_VIGENTES("fichas.dominio.estadofichaperfil.error.sin-estudiantes-vigentes", 1),
    LOG_AGREGANDO_APROBACION("fichas.aplicacion.estadofichaperfil.log.agregando-aprobacion", 2),
    LOG_VERIFICACION_APROBACION("fichas.aplicacion.estadofichaperfil.log.verificacion-aprobacion", 7),
    LOG_APROBACION_AGREGADA("fichas.aplicacion.estadofichaperfil.log.aprobacion-agregada", 3),
    ERROR_ESTADO_FICHA_INVALIDO("fichas.dominio.estadofichaperfil.error.estado-ficha-invalido", 1),
    ERROR_ESTADO_NO_ASIGNABLE_POR_ASESOR("fichas.dominio.estadofichaperfil.error.estado-no-asignable-por-asesor", 1),
    ERROR_ESTADO_REPETIDO("fichas.dominio.estadofichaperfil.error.estado-repetido", 1),
    ERROR_TRANSICION_NO_PERMITIDA("fichas.dominio.estadofichaperfil.error.transicion-no-permitida", 2),
    ERROR_EVALUACION_EN_CURSO("fichas.dominio.estadofichaperfil.error.evaluacion-en-curso", 2),
    LOG_AGREGANDO("fichas.aplicacion.estadofichaperfil.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.estadofichaperfil.log.verificacion-agregar", 4),
    LOG_AGREGADO("fichas.aplicacion.estadofichaperfil.log.agregado", 3);

    private final String clave;
    private final int parametros;

    EstadoFichaPerfilKey(String clave, int parametros) {
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
