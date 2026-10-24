package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class PepPropertiesTest {

    private static PepProperties con(String url, boolean inseguro, String app, String env, String cred) {
        return new PepProperties(url, app, env, cred, inseguro, Duration.ofSeconds(1), Duration.ofSeconds(3), 0,
                Duration.ofMillis(200));
    }

    @Test
    void debeNoTenerProblemas_cuandoHttpsYDatosCompletos() {
        // Act / Assert
        assertThat(con("https://pep.uco.edu.co", false, "app", "prod", "c").problemas()).isEmpty();
        assertThat(con("http://localhost:18081", true, "app", "dev", "c").problemas()).isEmpty();
    }

    @Test
    void debeReportarProblemas_cuandoHttpInseguroOUrlConUserinfoOFaltanDatos() {
        // Act / Assert
        assertThat(con("http://pep", false, "app", "prod", "c").problemas()).hasSize(1);
        assertThat(con("https://u:p@pep?x=1", false, "app", "prod", "c").problemas()).hasSize(1);
        assertThat(con("", false, "", "", "").problemas()).hasSize(4);
        assertThat(con("ht tp://mal formada", false, "app", "prod", "c").problemas()).isNotEmpty();
    }

    @Test
    void debeEnmascararLaCredencial_cuandoSeImprime() {
        // Act / Assert
        assertThat(con("https://pep", false, "app", "prod", "super-secreto").toString())
                .doesNotContain("super-secreto");
    }
}
