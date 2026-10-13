package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionEstadoFichaPerfilDomainTest {

    @Test
    void debeCrearLaAgregacion_cuandoEstadoYAsesorEstanPresentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var estado = EstadoFichaPerfilDomain.crearPorAsesor(fichaPerfil, "DESCARTADA");

        // Act
        var agregacion = AgregacionEstadoFichaPerfilDomain.crear(estado, asesorFicha);

        // Assert
        assertThat(agregacion.getEstado()).isSameAs(estado);
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
        assertThat(agregacion.getFichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoEstadoYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstadoFichaPerfilDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ESTADO_FICHA);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ASESOR_FICHA);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO);
                    });
                });
    }
}
