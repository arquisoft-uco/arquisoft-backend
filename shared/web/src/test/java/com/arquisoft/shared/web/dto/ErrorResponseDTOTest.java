package com.arquisoft.shared.web.dto;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDTOTest {

    @Test
    void debeExponerElMensajeYNoElDetalle_cuandoLaExcepcionTraeDetalle() {
        // Arrange
        var excepcion = new DomainException(BaseError.conDetalle(
                "CODIGO", "El artefacto solicitado no existe", "No existe el artefacto 184b1e27"));

        // Act
        var respuesta = ErrorResponseDTO.fromBaseException(
                excepcion, "Error de dominio", HttpStatus.UNPROCESSABLE_CONTENT, "/artefactos/184b1e27/versiones");

        // Assert
        assertThat(respuesta.getMessage()).isEqualTo("El artefacto solicitado no existe");
        assertThat(respuesta.getErrorCode()).isEqualTo("CODIGO");
        assertThat(respuesta.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }
}
