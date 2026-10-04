package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.RegistrarProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoVigenteException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarProyectoGradoValidatorTest {

    private final RegistrarProyectoGradoValidatorImpl validator = new RegistrarProyectoGradoValidatorImpl();

    @Test
    void debeLanzarCoordinadorNoVigente_cuandoElCoordinadorNoEstaEnLaReplica() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), CoordinadorDomain.VACIO))
                .isInstanceOf(CoordinadorNoVigenteException.class);
    }

    @Test
    void debePasar_cuandoElCoordinadorEstaVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var coordinador = CoordinadorDomain.reconstruir(id, "1020", "Laura Mesa", "laura.mesa@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), null);

        // Act & Assert
        assertThatCode(() -> validator.validar(id, coordinador)).doesNotThrowAnyException();
    }
}
