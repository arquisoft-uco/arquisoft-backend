package com.arquisoft.shared.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseErrorTest {

    @Test
    void debeUsarElMensajeComoDetalle_cuandoNoSeDeclaraDetalle() {
        // Act
        var error = BaseError.of("CODIGO", "El artefacto solicitado no existe");

        // Assert
        assertThat(error.getMessage()).isEqualTo("El artefacto solicitado no existe");
        assertThat(error.getDetalle()).isEqualTo("El artefacto solicitado no existe");
    }

    @Test
    void debeSepararMensajeYDetalle_cuandoSeDeclaraDetalle() {
        // Act
        var excepcion = new DomainException(BaseError.conDetalle(
                "CODIGO", "El artefacto solicitado no existe", "No existe el artefacto 184b1e27"));

        // Assert
        assertThat(excepcion.getError().getMessage()).isEqualTo("El artefacto solicitado no existe");
        assertThat(excepcion.getMessage()).isEqualTo("No existe el artefacto 184b1e27");
        assertThat(excepcion.getCodigoError()).isEqualTo("CODIGO");
    }

    @Test
    void debeConservarElMensaje_cuandoSeUsaElConstructorDeCompatibilidad() {
        // Act
        var excepcion = new DomainException("El artefacto solicitado no existe", "CODIGO");

        // Assert
        assertThat(excepcion.getMessage()).isEqualTo("El artefacto solicitado no existe");
        assertThat(excepcion.getError().getMessage()).isEqualTo("El artefacto solicitado no existe");
    }
}
