package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * El orden entre las reglas de negocio se prueba en RegistrarFichaPerfilValidatorTest:
 * aqui solo se comprueba que el caso de uso valida antes de persistir y que propaga
 * lo que lance el validador.
 */
@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private RegistrarFichaPerfilValidator registrarFichaPerfilValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private RegistrarFichaPerfilUseCaseImpl registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título de prueba");

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
    }

    @Test
    void debeGuardarEstadoInicial_cuandoValidacionesExitosas() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título válido");

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
        verify(estadoFichaPerfilOutputPort, times(1)).guardar(any());
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(UUID.randomUUID()));

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(registrarFichaPerfilValidator, fichaPerfilOutputPort);
        inOrder.verify(registrarFichaPerfilValidator).validar(any(), anyList(), anyList());
        inOrder.verify(fichaPerfilOutputPort).guardar(any(FichaPerfilAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorNoExiste() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título de prueba");
        doThrow(new AsesorFichaNoEncontradoException(command.asesorFicha()))
                .when(registrarFichaPerfilValidator).validar(any(), any(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        String titulo = "Título duplicado";
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes(titulo);
        doThrow(new FichaTituloDuplicadoException(titulo))
                .when(registrarFichaPerfilValidator).validar(any(), any(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_cuandoUUIDNoExiste() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(estudianteId));
        doThrow(new EstudianteNoEncontradoException(estudianteId))
                .when(registrarFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteDuplicado_cuandoUUIDRepetidoEnLista() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(estudianteId, estudianteId));
        doThrow(new EstudianteDuplicadoException(estudianteId))
                .when(registrarFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarLimiteExcedido_cuandoElValidadorFalla() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        doThrow(limiteExcedido())
                .when(registrarFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(CupoEstudiantesExcedidoException.class)
                .hasMessage(Messages.formatear(FichasKeys.EstudianteFichaPerfil.ERROR_LIMITE_EXCEDIDO, 
                        FichasLimits.FichaPerfil.ESTUDIANTES_MAX));

        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeConstruirAgregadoAntesDeValidar_cuandoTituloEsInvalido() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("   ");

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(registrarFichaPerfilValidator, never()).validar(any(), any(), anyList());
        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título de prueba");
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).guardar(any(FichaPerfilAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(InfrastructureException.class);
    }

    @Test
    void debeAsignarUnEstudiante_cuandoListaValida() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(UUID.randomUUID()));

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, times(1)).guardar(any(EstudianteFichaPerfilAggregate.class));
    }

    @Test
    void debeAsignarTresEstudiantes_cuandoListaValida() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(
                List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, times(3)).guardar(any(EstudianteFichaPerfilAggregate.class));
    }

    @Test
    void debeCrearFichaSinEstudiantes_cuandoListaEsNull() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título de prueba");

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeCrearFichaSinEstudiantes_cuandoListaEsVacia() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(new ArrayList<>());

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePersistirRelaciones_despuesDePersistirFicha() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(UUID.randomUUID()));

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilOutputPort, estudianteFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilOutputPort).guardar(any(FichaPerfilAggregate.class));
        inOrder.verify(estudianteFichaPerfilOutputPort).guardar(any(EstudianteFichaPerfilAggregate.class));
    }

    private RegistrarFichaPerfilCommand comandoSinEstudiantes(String titulo) {
        return new RegistrarFichaPerfilCommand(titulo, UUID.randomUUID(), null);
    }

    private RegistrarFichaPerfilCommand comandoConEstudiantes(List<UUID> estudiantes) {
        return new RegistrarFichaPerfilCommand("Título de prueba", UUID.randomUUID(), estudiantes);
    }

    private static CupoEstudiantesExcedidoException limiteExcedido() {
        return new CupoEstudiantesExcedidoException(FichasLimits.FichaPerfil.ESTUDIANTES_MAX);
    }
}
