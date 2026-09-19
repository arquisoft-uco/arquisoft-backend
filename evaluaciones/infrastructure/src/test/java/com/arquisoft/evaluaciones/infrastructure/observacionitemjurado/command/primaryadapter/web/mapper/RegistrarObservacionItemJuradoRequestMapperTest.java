package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.RegistrarObservacionItemJuradoRequestDTO;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionItemJuradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var dto = new RegistrarObservacionItemJuradoRequestDTO("Sustenta el puntaje otorgado");

        // Act
        var command = RegistrarObservacionItemJuradoRequestMapper.toCommand(dto, evaluacionCuantitativaJurado);

        // Assert
        assertThat(command.evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(command.descripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }

    @Test
    void debePropagarExcepcion_cuandoDescripcionEsInvalida() {
        // Arrange
        var dto = new RegistrarObservacionItemJuradoRequestDTO(" ");

        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemJuradoRequestMapper.toCommand(dto, UUID.randomUUID()))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
