package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorDeRevisionNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.shared.exception.DomainException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MensajeSinIdentificadoresTest {

    private static final Pattern UUID_EN_TEXTO = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    static Stream<Supplier<DomainException>> excepciones() {
        return Stream.of(
                AsesorDeRevisionNoEncontradoException::new,
                EstudianteNoEncontradoException::new,
                RevisionAsesorNoEncontradaException::new,
                RevisionAsesorCerradaException::new,
                () -> new EstadoDestinoRevisionAsesorNoPermitidoException("CERRADA"),
                EstudianteNoParticipaArtefactoException::new);
    }

    @ParameterizedTest
    @MethodSource("excepciones")
    void noDebeExponerIdentificadoresNiMarcadores_cuandoSeConstruyeElMensaje(Supplier<DomainException> fabrica) {
        // Act
        var mensaje = fabrica.get().getMessage();

        // Assert
        assertThat(mensaje).isNotBlank().doesNotContain("%s", "artefactos.dominio");
        assertThat(UUID_EN_TEXTO.matcher(mensaje).find()).isFalse();
    }
}
