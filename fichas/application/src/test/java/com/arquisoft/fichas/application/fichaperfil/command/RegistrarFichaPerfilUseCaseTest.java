package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.EstudiantesFichaValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private FichaPerfilValidator fichaPerfilValidator;

    @Mock
    private EstudiantesFichaValidator estudiantesFichaValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

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
    void debeLanzarExcepcion_cuandoAsesorNoExiste() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoSinEstudiantes("Título de prueba");
        doThrow(new AsesorFichaNoEncontradoException(command.asesorFicha()))
                .when(fichaPerfilValidator).validarAsesorExiste(command.asesorFicha());

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
                .when(fichaPerfilValidator).validarTituloUnico(titulo);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
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
    void debeLanzarDomainValidationException_cuandoMasDeTresEstudiantes() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        // Act
        Throwable ex = catchThrowable(() -> registrarFichaPerfilUseCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                        FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                ));

        DomainValidationException domainEx = (DomainValidationException) ex;
        assertThat(domainEx.getValidationResult().getErrors())
                .anyMatch(error -> error.errorCode().equals(
                        FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO
                ));
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_cuandoUUIDNoExiste() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(estudianteId));
        doThrow(new EstudianteNoEncontradoException(estudianteId))
                .when(estudiantesFichaValidator).validarExistencia(anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeValidarDuplicadosAntesDeConsultarLaBaseDeDatos_cuandoUUIDRepetidoEnLista() {
        // Arrange — la integridad de los datos se valida antes que cualquier acceso a BD
        UUID estudianteId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(
                Arrays.asList(estudianteId, estudianteId));
        doThrow(new EstudianteDuplicadoException(estudianteId))
                .when(estudiantesFichaValidator).validarSinDuplicados(anyList());

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteDuplicadoException.class);

        verify(fichaPerfilValidator, never()).validarAsesorExiste(any());
        verify(fichaPerfilValidator, never()).validarTituloUnico(any());
        verify(estudiantesFichaValidator, never()).validarExistencia(anyList());
        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeValidarIntegridadAntesQueExistencia_cuandoHayEstudiantes() {
        // Arrange
        RegistrarFichaPerfilCommand command = comandoConEstudiantes(List.of(UUID.randomUUID()));

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(estudiantesFichaValidator, fichaPerfilValidator);
        inOrder.verify(estudiantesFichaValidator).validarSinDuplicados(anyList());
        inOrder.verify(fichaPerfilValidator).validarAsesorExiste(any());
        inOrder.verify(fichaPerfilValidator).validarTituloUnico(any());
        inOrder.verify(estudiantesFichaValidator).validarExistencia(anyList());
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
}
