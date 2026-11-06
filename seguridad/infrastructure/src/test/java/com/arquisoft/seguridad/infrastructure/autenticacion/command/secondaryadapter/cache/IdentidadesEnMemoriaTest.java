package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class IdentidadesEnMemoriaTest {

    private static final ResolucionSesion.Resuelta ANA = new ResolucionSesion.Resuelta("s1", "Ana", "a@uco.edu.co", "t");

    private static final class RelojManual extends Clock {

        private Instant ahora = Instant.parse("2026-11-04T10:00:00Z");

        void avanzar(Duration duracion) {
            ahora = ahora.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }

    @Test
    void debeDevolverLaIdentidad_cuandoNoExpiro() {
        // Arrange
        var reloj = new RelojManual();
        var memoria = new IdentidadesEnMemoria(10, Duration.ofSeconds(60), reloj);
        memoria.guardar("h1", ANA);
        reloj.avanzar(Duration.ofSeconds(59));

        // Act
        var encontrada = memoria.obtener("h1");

        // Assert
        assertThat(encontrada).contains(ANA);
        assertThat(memoria.obtener("otra")).isEmpty();
    }

    @Test
    void debeExpirar_cuandoPasaElTtl() {
        // Arrange
        var reloj = new RelojManual();
        var memoria = new IdentidadesEnMemoria(10, Duration.ofSeconds(60), reloj);
        memoria.guardar("h1", ANA);
        reloj.avanzar(Duration.ofSeconds(60));

        // Act
        var encontrada = memoria.obtener("h1");

        // Assert
        assertThat(encontrada).isEmpty();
        assertThat(memoria.tamanio()).isZero();
    }

    @Test
    void debeDesalojarLaMasAntigua_cuandoSuperaElTope() {
        // Arrange
        var memoria = new IdentidadesEnMemoria(2, Duration.ofSeconds(60), new RelojManual());
        memoria.guardar("h1", ANA);
        memoria.guardar("h2", ANA);
        memoria.obtener("h1");

        // Act
        memoria.guardar("h3", ANA);

        // Assert
        assertThat(memoria.tamanio()).isEqualTo(2);
        assertThat(memoria.obtener("h2")).isEmpty();
        assertThat(memoria.obtener("h1")).isPresent();
        assertThat(memoria.obtener("h3")).isPresent();
    }
}
