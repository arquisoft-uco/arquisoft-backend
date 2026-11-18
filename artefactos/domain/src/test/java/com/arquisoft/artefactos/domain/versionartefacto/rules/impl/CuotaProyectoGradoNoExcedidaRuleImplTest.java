package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CuotaProyectoGradoExcedidaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CuotaProyectoGradoNoExcedidaRuleImplTest {

    private static final long CUOTA = 1_073_741_824L;

    private final CuotaProyectoGradoNoExcedidaRuleImpl regla = new CuotaProyectoGradoNoExcedidaRuleImpl();

    private static CuotaProyectoGrado cuota(UUID proyecto, long uso, long tamanoNuevo) {
        return new CuotaProyectoGrado(proyecto, "Sistema de gestion", "informe.pdf", CUOTA, uso, tamanoNuevo);
    }

    @Test
    void debePasar_cuandoSumaMenorQueCuota() {
        // Arrange
        var entrada = cuota(UtilUUID.generarNuevoUUID(), 1_000L, 2_000L);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoSumaIgualCuota() {
        // Arrange
        var entrada = cuota(UtilUUID.generarNuevoUUID(), CUOTA - 500L, 500L);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoSumaExcedeEnUnByte() {
        // Arrange
        var entrada = cuota(UtilUUID.generarNuevoUUID(), CUOTA - 500L, 501L);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(CuotaProyectoGradoExcedidaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.CUOTA_PROYECTO_GRADO_EXCEDIDA);
    }

    @Test
    void debeLanzarConDisponibleEnCero_cuandoUsoYaSuperaCuota() {
        // Arrange
        var entrada = cuota(UtilUUID.generarNuevoUUID(), CUOTA + 1_048_576L, 1L);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(CuotaProyectoGradoExcedidaException.class)
                .satisfies(ex -> assertThat(((CuotaProyectoGradoExcedidaException) ex).getError().getMessage())
                        .contains("quedan 0 MB disponibles de 1 GB"));
    }

    @Test
    void debeOcultarIdentificadores_enMensajeDeClienteYExponerlosEnDetalle() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = cuota(proyecto, CUOTA - 5_242_880L, 10_485_760L);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(CuotaProyectoGradoExcedidaException.class)
                .satisfies(ex -> {
                    var excepcion = (CuotaProyectoGradoExcedidaException) ex;
                    var mensaje = excepcion.getError().getMessage();
                    assertThat(mensaje).contains("informe.pdf", "Sistema de gestion", "5 MB", "1 GB");
                    assertThat(mensaje).doesNotContain(proyecto.toString());
                    assertThat(excepcion.getMessage()).contains(proyecto.toString());
                });
    }
}
