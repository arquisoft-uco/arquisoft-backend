package com.arquisoft.shared.jpa.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropiedadesJpaTest {

    @Test
    void debeValidarElEsquema_yNuncaModificarlo() {
        // Arrange & Act
        var propiedades = PropiedadesJpa.porDefecto();

        // Assert — 'update' o 'create' dejarían a Hibernate tocando un esquema que gobierna Flyway
        assertThat(propiedades).containsEntry("hibernate.hbm2ddl.auto", "validate");
    }

    @Test
    void debeDejarElVolcadoDeSqlApagado() {
        // Arrange & Act
        var propiedades = PropiedadesJpa.porDefecto();

        // Assert
        assertThat(propiedades).containsEntry("hibernate.show_sql", "false");
    }

    @Test
    void debeDeclararElTamanioDeLote() {
        // Arrange & Act
        var propiedades = PropiedadesJpa.porDefecto();

        // Assert
        assertThat(propiedades).containsEntry("hibernate.jdbc.batch_size", "25");
    }
}
