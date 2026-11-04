package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class MapeoRolesPropertiesTest {

    private static MapeoRolesProperties con(Duration refresco, Duration ttl, Duration sondeo, Duration reserva) {
        return new MapeoRolesProperties(refresco, ttl, sondeo, reserva);
    }

    @Test
    void debeNoTenerProblemas_cuandoSonLosValoresPorDefecto() {
        // Act / Assert
        assertThat(con(Duration.ofMinutes(5), Duration.ofMinutes(15), Duration.ofSeconds(30), Duration.ofSeconds(60))
                .problemas()).isEmpty();
    }

    @Test
    void debeReportar_cuandoElTtlEsMenorQueDosVecesElIntervalo() {
        // Act
        var problemas = con(Duration.ofMinutes(5), Duration.ofMinutes(9), Duration.ofSeconds(30), Duration.ofSeconds(60))
                .problemas();

        // Assert
        assertThat(problemas).hasSize(1).first().asString().contains("ttl");
    }

    @Test
    void debeReportar_cuandoElSondeoSuperaElIntervalo() {
        // Act
        var problemas = con(Duration.ofMinutes(5), Duration.ofMinutes(15), Duration.ofMinutes(6), Duration.ofSeconds(60))
                .problemas();

        // Assert
        assertThat(problemas).hasSize(1).first().asString().contains("check-interval");
    }

    @Test
    void debeReportar_cuandoUnaDuracionNoEsPositiva() {
        // Act
        var problemas = con(Duration.ZERO, Duration.ofMinutes(15), Duration.ofSeconds(30), Duration.ofSeconds(-1))
                .problemas();

        // Assert
        assertThat(problemas).hasSize(2);
    }
}
