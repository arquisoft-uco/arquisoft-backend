package com.arquisoft.shared.message.annotation;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/**
 * Referencias a los mensajes de {@code ValidationMessages.properties}, listas para usar en el
 * atributo {@code message} de una restricción Jakarta.
 *
 * <p>Cada constante lleva la clave envuelta en llaves ({@code "{clave}"}): esa es la sintaxis que
 * Hibernate Validator interpreta como «resuelve esto contra el bundle» en tiempo de ejecución.
 * El valor sigue siendo una constante de compilación, que es lo único que una anotación admite,
 * pero el texto vive fuera del código.
 *
 * <p>Por esa misma razón estas claves no pueden ser un {@link ClaveMensaje} como las de
 * {@code key.*}: una llamada a {@link ClaveMensaje#clave()} no es una expresión constante. Apuntan
 * además a otro bundle y las resuelve otro mecanismo — {@code ValidationMessages.properties} lo
 * lee Hibernate Validator, no {@link CatalogoMensajes}.
 */
public final class ValidationKeys {

    private ValidationKeys() {}

    private static final String LLAVE_APERTURA = "{";
    private static final String LLAVE_CIERRE = "}";

    /**
     * Devuelve la clave desnuda a partir de una de las referencias de esta clase.
     *
     * <p>Lo usan las pruebas: la anotación necesita la forma {@code "{clave}"}, pero para afirmar
     * sobre el texto resuelto hace falta la clave tal cual, que es lo que espera
     * {@code CatalogoMensajesResourceBundle.contieneClave(String)} — la vía para las claves que no
     * declaran bundle porque no son {@link ClaveMensaje}.
     *
     * @param referencia constante de esta clase, en la forma {@code "{clave}"}
     * @return la clave sin las llaves
     */
    public static String sinLlaves(String referencia) {
        return referencia.substring(LLAVE_APERTURA.length(), referencia.length() - LLAVE_CIERRE.length());
    }

    public static final class Http {

        private Http() {}

        public static final String UUID_INVALIDO = LLAVE_APERTURA + "app.infraestructura.http.validacion.uuid-invalido" + LLAVE_CIERRE;
    }
}
