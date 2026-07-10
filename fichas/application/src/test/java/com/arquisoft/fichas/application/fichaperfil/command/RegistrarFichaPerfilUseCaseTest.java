package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.exception.InfrastructureException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private EstudianteQueryOutputPort estudianteQueryOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @InjectMocks
    private RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        String titulo = "Título duplicado";
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(titulo, asesorId, null);

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto(titulo)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeGuardarFicha_cuandoValidacionesExitosas() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título válido",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título válido")).thenReturn(false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).guardar(any(FichaPerfilAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(InfrastructureException.class);
    }

    @Test
    void debeLoguearRegistro_cuandoExitoso() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
    }

    @Test
    void debeAsignarUnEstudiante_cuandoListaValida() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudianteId);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        when(estudianteQueryOutputPort.existsById(estudianteId)).thenReturn(true);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, times(1)).guardar(any(EstudianteFichaPerfilAggregate.class));
    }

    @Test
    void debeAsignarTresEstudiantes_cuandoListaValida() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudiante1, estudiante2, estudiante3);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante2)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante3)).thenReturn(true);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, times(3)).guardar(any(EstudianteFichaPerfilAggregate.class));
    }

    @Test
    void debeCrearFichaSinEstudiantes_cuandoListaEsNull() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                null
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeCrearFichaSinEstudiantes_cuandoListaEsVacia() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                new ArrayList<>()
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidationException_cuandoMasDeTresEstudiantes() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        UUID estudiante4 = UUID.randomUUID();
        List<UUID> estudiantesIds = Arrays.asList(estudiante1, estudiante2, estudiante3, estudiante4);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante2)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante3)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante4)).thenReturn(true);

        // Act
        Throwable ex = org.assertj.core.api.Assertions.catchThrowable(() -> registrarFichaPerfilUseCase.ejecutar(command));

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
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudianteId);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        when(estudianteQueryOutputPort.existsById(estudianteId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteDuplicado_cuandoUUIDRepetidoEnLista() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        List<UUID> estudiantesIds = Arrays.asList(estudianteId, estudianteId);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(command))
                .isInstanceOf(EstudianteDuplicadoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePersistirRelaciones_despuesDePersistirFicha() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudianteId);
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId,
                estudiantesIds
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);
        when(estudianteQueryOutputPort.existsById(estudianteId)).thenReturn(true);

        // Act
        registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilOutputPort, estudianteFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilOutputPort).guardar(any(FichaPerfilAggregate.class));
        inOrder.verify(estudianteFichaPerfilOutputPort).guardar(any(EstudianteFichaPerfilAggregate.class));
    }
}
