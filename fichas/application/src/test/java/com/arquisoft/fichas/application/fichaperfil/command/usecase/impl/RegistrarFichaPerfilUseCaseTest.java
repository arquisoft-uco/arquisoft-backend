package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * La asignación de estudiantes y del estado inicial ya no ocurren aquí: el
 * interactor las orquesta llamando a AsignarEstudiantesFichaPerfilUseCase y a
 * AsignarEstadoInicialFichaPerfilUseCase después de este caso de uso (ver
 * cobertura de esos escenarios en AsignarEstudiantesFichaPerfilUseCaseTest,
 * AsignarEstudiantesFichaPerfilValidatorTest y
 * AsignarEstadoInicialFichaPerfilUseCaseTest).
 */
@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private RegistrarFichaPerfilValidator registrarFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private RegistrarFichaPerfilUseCaseImpl registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        FichaPerfilDomain ficha = fichaValida();

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(ficha);

        // Assert
        assertThat(resultado).isEqualTo(ficha.getId());
        verify(fichaPerfilOutputPort, times(1)).registrarFicha(ficha);
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        FichaPerfilDomain ficha = fichaValida();

        // Act
        registrarFichaPerfilUseCase.ejecutar(ficha);

        // Assert
        InOrder inOrder = inOrder(registrarFichaPerfilValidator, fichaPerfilOutputPort);
        inOrder.verify(registrarFichaPerfilValidator).validar(ficha);
        inOrder.verify(fichaPerfilOutputPort).registrarFicha(ficha);
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorNoExiste() {
        // Arrange
        FichaPerfilDomain ficha = fichaValida();
        doThrow(new AsesorFichaNoEncontradoException(ficha.getAsesorFicha()))
                .when(registrarFichaPerfilValidator).validar(ficha);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(ficha))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        FichaPerfilDomain ficha = fichaValida();
        doThrow(new FichaTituloDuplicadoException(ficha.getTituloProyecto()))
                .when(registrarFichaPerfilValidator).validar(ficha);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(ficha))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        FichaPerfilDomain ficha = fichaValida();
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).registrarFicha(ficha);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(ficha))
                .isInstanceOf(InfrastructureException.class);
    }

    private FichaPerfilDomain fichaValida() {
        return FichaPerfilDomain.crear("Título de prueba", UUID.randomUUID());
    }
}
