package com.arquisoft.shared.jpa.config;

import java.util.Map;

// Cada contexto arma su propio EntityManagerFactory, pero las propiedades de Hibernate son
// las mismas para todos: si divergen, dos contextos migran distinto contra el mismo Postgres.
// Centralizarlas aquí hace que ese ajuste se toque una vez y no una por contexto.
public final class PropiedadesJpa {

    private static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String FORMAT_SQL = "hibernate.format_sql";
    private static final String JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
    private static final String SHOW_SQL = "hibernate.show_sql";

    // 'validate' y no 'update': el esquema lo gobierna Flyway, Hibernate solo comprueba
    // que las entidades cuadren con lo que las migraciones dejaron.
    private static final String VALIDAR_ESQUEMA = "validate";
    private static final String HABILITADO = "true";
    private static final String DESHABILITADO = "false";
    private static final String TAMANIO_LOTE = "25";

    private PropiedadesJpa() {}

    public static Map<String, Object> porDefecto() {
        return Map.of(
                HBM2DDL_AUTO, VALIDAR_ESQUEMA,
                FORMAT_SQL, HABILITADO,
                JDBC_BATCH_SIZE, TAMANIO_LOTE,
                SHOW_SQL, DESHABILITADO);
    }
}
