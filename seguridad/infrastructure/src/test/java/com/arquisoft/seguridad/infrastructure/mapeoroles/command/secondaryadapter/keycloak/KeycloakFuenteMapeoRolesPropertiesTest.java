package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakFuenteMapeoRolesPropertiesTest {

    private static final String UUID_CLIENTE = "cffeb896-738c-4000-80cf-8cf99e05233f";

    private static KeycloakFuenteMapeoRolesProperties con(String url, String realm, String clientId, String secreto,
                                                          String uuid) {
        return new KeycloakFuenteMapeoRolesProperties(url, realm, clientId, secreto, uuid, Duration.ofSeconds(2),
                Duration.ofSeconds(3));
    }

    @Test
    void debeNoTenerProblemas_cuandoLosDatosEstanCompletos() {
        // Act / Assert
        assertThat(con("http://kc", "arquisoft", "arquisoft-api", "s", UUID_CLIENTE).problemas()).isEmpty();
    }

    @Test
    void debeReportarCadaDatoVacio() {
        // Act / Assert
        assertThat(con("", "", "", "", "").problemas()).hasSize(5);
    }

    @Test
    void debeReportar_cuandoElClientUuidNoEsUuid() {
        // Act
        var problemas = con("http://kc", "arquisoft", "arquisoft-api", "s", "arquisoft-api").problemas();

        // Assert
        assertThat(problemas).hasSize(1).first().asString().contains("UUID");
    }

    @Test
    void debeEnmascararElSecreto_cuandoSeImprime() {
        // Act
        var texto = con("http://kc", "arquisoft", "arquisoft-api", "super-secreto", UUID_CLIENTE).toString();

        // Assert
        assertThat(texto).doesNotContain("super-secreto").contains("clientSecret=***");
    }
}
