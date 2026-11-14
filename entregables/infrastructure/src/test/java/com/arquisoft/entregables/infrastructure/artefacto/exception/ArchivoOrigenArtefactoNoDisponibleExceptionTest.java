package com.arquisoft.entregables.infrastructure.artefacto.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoOrigenArtefactoNoDisponibleExceptionTest {

    @Test
    void debeSerTransitoriaYNombrarElDocumentoYLaClave_cuandoFaltaElArchivoOrigen() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act
        var excepcion = new ArchivoOrigenArtefactoNoDisponibleException(artefacto, "Anteproyecto", "versiones/x");

        // Assert
        assertThat(excepcion).isInstanceOf(InfrastructureException.class);
        assertThat(excepcion.getCodigoError()).isEqualTo(EntregablesCodes.Artefacto.ARCHIVO_ORIGEN_NO_DISPONIBLE);
        assertThat(excepcion.getMessage()).contains("versiones/x", artefacto.toString());
    }
}
