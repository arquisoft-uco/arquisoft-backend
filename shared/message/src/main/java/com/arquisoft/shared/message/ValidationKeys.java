package com.arquisoft.shared.message;

/**
 * Referencias a los mensajes de {@code ValidationMessages.properties}, listas para usar en el
 * atributo {@code message} de una restricción Jakarta.
 *
 * <p>Cada constante lleva la clave envuelta en llaves ({@code "{clave}"}): esa es la sintaxis que
 * Hibernate Validator interpreta como «resuelve esto contra el bundle» en tiempo de ejecución.
 * El valor sigue siendo una constante de compilación, que es lo único que una anotación admite,
 * pero el texto vive fuera del código.
 *
 * <p>Están separadas de {@link FichasKeys} porque apuntan a otro bundle y las resuelve otro
 * mecanismo: {@code ValidationMessages.properties} lo lee Hibernate Validator, no
 * {@link MessageCatalog}.
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
     * {@link MessageCatalog#obtener(String)}.
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

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String TITULO_OBLIGATORIO =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.titulo-obligatorio" + LLAVE_CIERRE;
        public static final String TITULO_MAXIMO =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.titulo-maximo" + LLAVE_CIERRE;
        public static final String ASESOR_OBLIGATORIO =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.asesor-obligatorio" + LLAVE_CIERRE;
        public static final String ASESOR_UUID =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.asesor-uuid" + LLAVE_CIERRE;
        public static final String ESTUDIANTES_MAXIMO =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.estudiantes-maximo" + LLAVE_CIERRE;
        public static final String ESTUDIANTE_UUID =
                LLAVE_APERTURA + "fichas.infraestructura.fichaperfil.validacion.estudiante-uuid" + LLAVE_CIERRE;
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String TIPO_OBLIGATORIO =
                LLAVE_APERTURA + "fichas.infraestructura.itemfichaperfil.validacion.tipo-obligatorio" + LLAVE_CIERRE;
        public static final String CONTENIDO_OBLIGATORIO =
                LLAVE_APERTURA + "fichas.infraestructura.itemfichaperfil.validacion.contenido-obligatorio" + LLAVE_CIERRE;
        public static final String CONTENIDO_MAXIMO =
                LLAVE_APERTURA + "fichas.infraestructura.itemfichaperfil.validacion.contenido-maximo" + LLAVE_CIERRE;
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String ESTUDIANTES_OBLIGATORIOS =
                LLAVE_APERTURA + "fichas.infraestructura.estudiantefichaperfil.validacion.estudiantes-obligatorios" + LLAVE_CIERRE;
        public static final String ESTUDIANTES_MAXIMO =
                LLAVE_APERTURA + "fichas.infraestructura.estudiantefichaperfil.validacion.estudiantes-maximo" + LLAVE_CIERRE;
        public static final String ESTUDIANTE_UUID =
                LLAVE_APERTURA + "fichas.infraestructura.estudiantefichaperfil.validacion.estudiante-uuid" + LLAVE_CIERRE;
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String EVALUACION_OBLIGATORIA =
                LLAVE_APERTURA + "fichas.infraestructura.estadoevaluacionficha.validacion.evaluacion-obligatoria" + LLAVE_CIERRE;
        public static final String EVALUACION_UUID =
                LLAVE_APERTURA + "fichas.infraestructura.estadoevaluacionficha.validacion.evaluacion-uuid" + LLAVE_CIERRE;
        public static final String ESTADO_OBLIGATORIO =
                LLAVE_APERTURA + "fichas.infraestructura.estadoevaluacionficha.validacion.estado-obligatorio" + LLAVE_CIERRE;
        public static final String ESTADO_MAXIMO =
                LLAVE_APERTURA + "fichas.infraestructura.estadoevaluacionficha.validacion.estado-maximo" + LLAVE_CIERRE;
    }
}
