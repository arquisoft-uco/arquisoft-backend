package com.arquisoft.biblioteca.domain.estadodocumentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.exception.EstadoDocumentacionNoEncontradoException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDocumentacionTest {

    @ParameterizedTest
    @CsvSource({
            "PENDIENTE, Pendiente",
            "PUBLICADO, Publicado",
            "NO_PUBLICADO, No Publicado"
    })
    void debeRetornarLaConstanteConIdYNombreDelMER_cuandoElIdEstaEnElCatalogo(String id, String nombre) {
        // Act
        var estado = EstadoDocumentacion.desde(id);

        // Assert
        assertThat(estado.name()).isEqualTo(id);
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getNombre()).isEqualTo(nombre);
        assertThat(EstadoDocumentacion.esValido(id)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ESTADO_INEXISTENTE", "VACIO"})
    void debeLanzarNoEncontrado_cuandoElIdNoEstaEnElCatalogo(String id) {
        // Arrange
        ThrowingCallable accion = () -> EstadoDocumentacion.desde(id);

        // Act & Assert
        assertThatThrownBy(accion)
                .isInstanceOfSatisfying(EstadoDocumentacionNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(BibliotecaCodes.EstadoDocumentacion.NO_ENCONTRADO));
        assertThat(EstadoDocumentacion.esValido(id)).isFalse();
    }

    @Test
    void debeLlevarElIdRecibidoSoloAlDetalle_cuandoSeLanzaLaExcepcion() {
        // Act
        var ex = new EstadoDocumentacionNoEncontradoException("ESTADO_INEXISTENTE");

        // Assert
        assertThat(ex.getMessage()).contains("ESTADO_INEXISTENTE");
        assertThat(ex.getError().getMessage()).doesNotContain("ESTADO_INEXISTENTE");
    }
}
