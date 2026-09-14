package com.arquisoft.evaluaciones.domain.entregableproyectoacceso;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoAccesoDomainTest {

    @Test
    void debeCrearAccesoActivo_cuandoDatosValidos() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        EntregableProyectoAccesoDomain acceso =
                EntregableProyectoAccesoDomain.crear(entregable, proyecto, 1, ocurridoEn);

        // Assert
        assertThat(acceso.getEntregable()).isEqualTo(entregable);
        assertThat(acceso.getProyecto()).isEqualTo(proyecto);
        assertThat(acceso.getVersionEntregable()).isEqualTo(1);
        assertThat(acceso.isActivo()).isTrue();
        assertThat(acceso.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(acceso.esVacio()).isFalse();
    }

    @Test
    void debeLanzarExcepcion_cuandoVersionEntregableEsMenorAUno() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableProyectoAccesoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), 0, Instant.now()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EntregableProyectoAcceso.VERSION_ENTREGABLE_INVALIDA));
    }

    @Test
    void debeIdentificarseComoVacio_cuandoEsLaConstanteVacio() {
        // Act & Assert
        assertThat(EntregableProyectoAccesoDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeSerMasReciente_cuandoNoExisteProyeccionPreviaOElEventoEsPosterior() {
        // Arrange
        Instant ahora = Instant.now();
        EntregableProyectoAccesoDomain nuevo = EntregableProyectoAccesoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), 1, ahora);
        EntregableProyectoAccesoDomain existenteAnterior = EntregableProyectoAccesoDomain.reconstruir(
                nuevo.getEntregable(), nuevo.getProyecto(), 1, true, ahora.minusSeconds(60));

        // Act & Assert
        assertThat(nuevo.esMasRecienteQue(EntregableProyectoAccesoDomain.VACIO)).isTrue();
        assertThat(nuevo.esMasRecienteQue(existenteAnterior)).isTrue();
    }

    @Test
    void noDebeSerMasReciente_cuandoElEventoEsAnteriorOTieneElMismoInstante() {
        // Arrange
        Instant ahora = Instant.now();
        EntregableProyectoAccesoDomain evento = EntregableProyectoAccesoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), 1, ahora);
        EntregableProyectoAccesoDomain existentePosterior = EntregableProyectoAccesoDomain.reconstruir(
                evento.getEntregable(), evento.getProyecto(), 2, true, ahora.plusSeconds(60));
        EntregableProyectoAccesoDomain existenteIgual = EntregableProyectoAccesoDomain.reconstruir(
                evento.getEntregable(), evento.getProyecto(), 1, true, ahora);

        // Act & Assert
        assertThat(evento.esMasRecienteQue(existentePosterior)).isFalse();
        assertThat(evento.esMasRecienteQue(existenteIgual)).isFalse();
    }
}
