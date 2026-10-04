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
    LOG_APROBACION_AGREGADA("fichas.aplicacion.estadofichaperfil.log.aprobacion-agregada", 3);

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
