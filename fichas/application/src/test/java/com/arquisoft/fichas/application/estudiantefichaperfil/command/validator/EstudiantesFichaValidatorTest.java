package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesFichaValidatorTest {

    @Mock
    private EstudianteQueryOutputPort estudianteQueryOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private EstudiantesFichaValidator validator;

    @Test
    void debeLanzarExcepcion_cuandoHayEstudianteRepetido() {
        // Arrange
        UUID repetido = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validarSinDuplicados(List.of(repetido, repetido)))
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(repetido.toString());
    }

    @Test
    void debePasar_cuandoNoHayRepetidos() {
        // Act & Assert
        assertThatCode(() -> validator.validarSinDuplicados(List.of(UUID.randomUUID(), UUID.randomUUID())))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoListaEsNula() {
        assertThatCode(() -> validator.validarSinDuplicados(null)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validarExistencia(null)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validarNoVinculados(UUID.randomUUID(), null))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoExiste() {
        // Arrange
        UUID inexistente = UUID.randomUUID();
        when(estudianteQueryOutputPort.existePorId(inexistente)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarExistencia(List.of(inexistente)))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoTodosLosEstudiantesExisten() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        when(estudianteQueryOutputPort.existePorId(estudiante)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarExistencia(List.of(estudiante))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteYaEstaVinculado() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(ficha, estudiante)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarNoVinculados(ficha, List.of(estudiante)))
                .isInstanceOf(EstudianteDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoNingunEstudianteEstaVinculado() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(ficha, estudiante)).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> validator.validarNoVinculados(ficha, List.of(estudiante)))
                .doesNotThrowAnyException();
    }
}
