package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilValidatorTest {

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteRule fichaPerfilExisteRule;

    @InjectMocks
    private FichaPerfilValidator validator;

    @Test
    void debeLanzarExcepcion_cuandoAsesorNoExiste() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        when(asesorFichaQueryOutputPort.existePorId(asesor)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarAsesorExiste(asesor))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoAsesorExiste() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        when(asesorFichaQueryOutputPort.existePorId(asesor)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarAsesorExiste(asesor)).doesNotThrowAnyException();
    }

    @Test
    void debeDelegarEnLaReglaDeExistencia_cuandoValidaFichaExiste() {
        // Arrange
        UUID ficha = UUID.randomUUID();

        // Act
        validator.validarFichaExiste(ficha);

        // Assert
        verify(fichaPerfilExisteRule).validar(ficha);
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloYaExiste() {
        // Arrange
        when(fichaPerfilOutputPort.existePorTituloProyecto("Título")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarTituloUnico("Título"))
                .isInstanceOf(FichaTituloDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoTituloEsUnico() {
        // Arrange
        when(fichaPerfilOutputPort.existePorTituloProyecto("Título")).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> validator.validarTituloUnico("Título")).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        var criteria = new PropietarioFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(criteria)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarEstudiantePropietario(criteria))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debePasar_cuandoEstudianteEsPropietario() {
        // Arrange
        var criteria = new PropietarioFichaCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(criteria)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarEstudiantePropietario(criteria)).doesNotThrowAnyException();
    }
}
