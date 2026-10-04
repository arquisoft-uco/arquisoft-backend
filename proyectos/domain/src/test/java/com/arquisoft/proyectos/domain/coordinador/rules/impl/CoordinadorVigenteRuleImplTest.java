package com.arquisoft.proyectos.domain.coordinador.rules.impl;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoVigenteException;
import com.arquisoft.proyectos.domain.coordinador.model.VigenciaCoordinador;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorVigenteRuleImplTest {

    private final CoordinadorVigenteRuleImpl regla = new CoordinadorVigenteRuleImpl();

    private static CoordinadorDomain coordinador(UUID id, Instant eliminadoEn) {
        return CoordinadorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    @Test
    void debeLanzarExcepcion_cuandoElCoordinadorNoExisteEnLaReplica() {
        // Arrange
        var vigencia = new VigenciaCoordinador(UUID.randomUUID(), CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(CoordinadorNoVigenteException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.ProyectoGrado.COORDINADOR_NO_VIGENTE);
    }

    @Test
    void debeLanzarExcepcion_cuandoElCoordinadorFueDadoDeBaja() {
        // Arrange
        var id = UUID.randomUUID();
        var vigencia = new VigenciaCoordinador(id, coordinador(id, Instant.parse("2026-09-20T10:00:00Z")));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(CoordinadorNoVigenteException.class);
    }

    @Test
    void debePasar_cuandoElCoordinadorExisteYEstaVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var vigencia = new VigenciaCoordinador(id, coordinador(id, null));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
