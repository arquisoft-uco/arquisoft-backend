package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/**
 * Consultas dinámicas: filtros, ordenamiento y traducción a JPA.
 *
 * <p>Las claves {@code dominio.*} las produce {@code shared:domain} al validar lo que llega del
 * cliente; las {@code infraestructura.*}, {@code shared:postgres} al traducirlo a Criteria API y
 * {@code shared:web} al deserializar el árbol de filtros.
 *
 * <p>Las de {@code tipo.*} no son mensajes completos sino la etiqueta del tipo de campo que se
 * sustituye dentro de {@link #ERROR_OPERADOR_NO_APLICABLE}. Están en el catálogo porque terminan
 * en un texto que ve el cliente.
 */
public enum ConsultaKey implements ClaveMensaje {

    ERROR_CONECTOR_INVALIDO("app.dominio.consulta.error.conector-invalido"),
    ERROR_OPERADOR_INVALIDO("app.dominio.consulta.error.operador-invalido"),
    ERROR_CAMPO_ORDEN_NO_PERMITIDO("app.dominio.consulta.error.campo-orden-no-permitido"),
    ERROR_PROFUNDIDAD_FILTRO_EXCEDIDA("app.dominio.consulta.error.profundidad-filtro-excedida"),
    ERROR_CAMPO_FILTRO_NO_PERMITIDO("app.dominio.consulta.error.campo-filtro-no-permitido"),
    ERROR_VALOR_REQUERIDO("app.dominio.consulta.error.valor-requerido"),
    ERROR_ORDEN_CAMPO_VACIO("app.dominio.consulta.error.orden-campo-vacio"),
    ERROR_ORDEN_DIRECCION_INVALIDA("app.dominio.consulta.error.orden-direccion-invalida"),

    ERROR_CAMPO_FILTRO_DESCONOCIDO("app.infraestructura.consulta.error.campo-filtro-desconocido"),
    ERROR_OPERADOR_NO_APLICABLE("app.infraestructura.consulta.error.operador-no-aplicable"),
    ERROR_UUID_INVALIDO("app.infraestructura.consulta.error.uuid-invalido"),
    ERROR_ENTERO_INVALIDO("app.infraestructura.consulta.error.entero-invalido"),
    ERROR_DECIMAL_INVALIDO("app.infraestructura.consulta.error.decimal-invalido"),
    ERROR_FECHA_INVALIDA("app.infraestructura.consulta.error.fecha-invalida"),
    ERROR_FECHA_HORA_INVALIDA("app.infraestructura.consulta.error.fecha-hora-invalida"),
    ERROR_BOOLEANO_INVALIDO("app.infraestructura.consulta.error.booleano-invalido"),
    ERROR_RUTA_ORDEN_NO_MAPEADA("app.infraestructura.consulta.error.ruta-orden-no-mapeada"),
    ERROR_CONECTOR_REQUERIDO("app.infraestructura.consulta.error.conector-requerido"),

    TIPO_TEXTO("app.infraestructura.consulta.tipo.texto"),
    TIPO_UUID("app.infraestructura.consulta.tipo.uuid"),
    TIPO_ENTERO("app.infraestructura.consulta.tipo.entero"),
    TIPO_DECIMAL("app.infraestructura.consulta.tipo.decimal"),
    TIPO_FECHA("app.infraestructura.consulta.tipo.fecha"),
    TIPO_FECHA_HORA("app.infraestructura.consulta.tipo.fecha-hora"),
    TIPO_BOOLEANO("app.infraestructura.consulta.tipo.booleano");

    private final String clave;

    ConsultaKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
