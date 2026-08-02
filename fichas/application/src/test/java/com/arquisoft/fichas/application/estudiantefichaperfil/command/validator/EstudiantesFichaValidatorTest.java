package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudiantesCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

/**
 * El validador solo delega: la lógica de cada regla se prueba en fichas:domain,
 * en los tests de los respectivos RuleImpl.
 */
@ExtendWith(MockitoExtension.class)
class EstudiantesFichaValidatorTest {

    @Mock
    private EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;

    @Mock
    private EstudiantesExistenRule estudiantesExistenRule;

    @Mock
    private EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;

    @InjectMocks
    private EstudiantesFichaValidator validator;

    @Test
    void debeDelegarEnLaReglaDeDuplicados_cuandoValidaSinDuplicados() {
        // Arrange
        List<UUID> estudiantes = List.of(UUID.randomUUID());

        // Act
        validator.validarSinDuplicados(estudiantes);

        // Assert
        verify(estudiantesSinDuplicadosRule).validar(estudiantes);
    }

    @Test
    void debeDelegarEnLaReglaDeExistencia_cuandoValidaExistencia() {
        // Arrange
        List<UUID> estudiantes = List.of(UUID.randomUUID());

        // Act
        validator.validarExistencia(estudiantes);

        // Assert
        verify(estudiantesExistenRule).validar(estudiantes);
    }

    @Test
    void debeDelegarEnLaReglaDeVinculacion_cuandoValidaNoVinculados() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        List<UUID> estudiantes = List.of(UUID.randomUUID());

        // Act
        validator.validarNoVinculados(fichaPerfil, estudiantes);

        // Assert
        verify(estudiantesNoVinculadosRule)
                .validar(new VinculacionEstudiantesCriteria(fichaPerfil, estudiantes));
    }
}
