package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Textos de los correos que produce el contexto. */
public enum PlantillaKey implements ClaveMensaje {

    ASUNTO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.asunto.asesor-cambiado", 1),
    CUERPO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.cuerpo.asesor-cambiado", 2),
    ASUNTO_FICHA_REGISTRADA_ASESOR(
            "notificaciones.aplicacion.plantilla.asunto.ficha-registrada-asesor", 1),
    CUERPO_FICHA_REGISTRADA_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-registrada-asesor", 2),
    ASUNTO_ESTUDIANTES_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.asunto.estudiantes-asignados", 1),
    CUERPO_ESTUDIANTES_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.cuerpo.estudiantes-asignados", 2),
    ASUNTO_SOLICITUD_NOVEDAD_COORDINADOR(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-novedad-coordinador", 1),
    CUERPO_SOLICITUD_NOVEDAD_COORDINADOR(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-novedad-coordinador", 3),
    ASUNTO_SOLICITUD_NOVEDAD_ASESOR(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-novedad-asesor", 1),
    CUERPO_SOLICITUD_NOVEDAD_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-novedad-asesor", 3),
    ASUNTO_SOLICITUD_CAMBIO_ASESOR(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-cambio-asesor", 1),
    CUERPO_SOLICITUD_CAMBIO_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-cambio-asesor", 3),
    ASUNTO_SOLICITUD_AMPLIACION_PLAZO(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-ampliacion-plazo", 1),
    CUERPO_SOLICITUD_AMPLIACION_PLAZO(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-ampliacion-plazo", 3),
    ASUNTO_SOLICITUD_NOVEDAD_COORDINADOR_RESPONDIDA(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-novedad-coordinador-respondida", 0),
    CUERPO_SOLICITUD_NOVEDAD_COORDINADOR_RESPONDIDA(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-novedad-coordinador-respondida", 3),
    ASUNTO_SOLICITUD_NOVEDAD_COORDINADOR_ESTADO_MODIFICADO(
            "notificaciones.aplicacion.plantilla.asunto.solicitud-novedad-coordinador-estado-modificado", 0),
    CUERPO_SOLICITUD_NOVEDAD_COORDINADOR_ESTADO_MODIFICADO(
            "notificaciones.aplicacion.plantilla.cuerpo.solicitud-novedad-coordinador-estado-modificado", 3),
    ASUNTO_REVISION_ITEM_AGREGADA(
            "notificaciones.aplicacion.plantilla.asunto.revision-item-agregada", 1),
    CUERPO_REVISION_ITEM_AGREGADA(
            "notificaciones.aplicacion.plantilla.cuerpo.revision-item-agregada", 2),
    ASUNTO_USUARIO_ESTADO_CAMBIADO(
            "notificaciones.aplicacion.plantilla.asunto.usuario-estado-cambiado", 1),
    CUERPO_USUARIO_ESTADO_CAMBIADO(
            "notificaciones.aplicacion.plantilla.cuerpo.usuario-estado-cambiado", 2),
    ASUNTO_FICHA_APROBADA("notificaciones.aplicacion.plantilla.asunto.ficha-aprobada", 1),
    CUERPO_FICHA_APROBADA_ESTUDIANTE(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-aprobada-estudiante", 3),
    CUERPO_FICHA_APROBADA_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-aprobada-asesor", 3),
    TEXTO_ESTADO_APROBADA("notificaciones.aplicacion.plantilla.valor.estado-aprobada", 0),
    TEXTO_ESTADO_APROBADA_CON_OBSERVACIONES(
            "notificaciones.aplicacion.plantilla.valor.estado-aprobada-con-observaciones", 0),
    ASUNTO_FICHA_NO_APROBADA("notificaciones.aplicacion.plantilla.asunto.ficha-no-aprobada", 1),
    CUERPO_FICHA_NO_APROBADA_ESTUDIANTE(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-no-aprobada-estudiante", 2),
    CUERPO_FICHA_NO_APROBADA_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-no-aprobada-asesor", 2),
    ASUNTO_PROYECTO_GRADO_REGISTRADO_COORDINADOR(
            "notificaciones.aplicacion.plantilla.asunto.proyecto-grado-registrado-coordinador", 1),
    CUERPO_PROYECTO_GRADO_REGISTRADO_COORDINADOR(
            "notificaciones.aplicacion.plantilla.cuerpo.proyecto-grado-registrado-coordinador", 2),
    ASUNTO_ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.asunto.estudiantes-proyecto-grado-asignados", 1),
    CUERPO_ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.cuerpo.estudiantes-proyecto-grado-asignados", 2),
    ASUNTO_ESTADO_FICHA_PERFIL_AGREGADO(
            "notificaciones.aplicacion.plantilla.asunto.estado-ficha-perfil-agregado", 2),
    CUERPO_ESTADO_FICHA_PERFIL_AGREGADO(
            "notificaciones.aplicacion.plantilla.cuerpo.estado-ficha-perfil-agregado", 3),
    PIE_GENERICO("notificaciones.aplicacion.plantilla.pie.generico", 0);

    private final String clave;
    private final int parametros;

    PlantillaKey(String clave, int parametros) {
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
