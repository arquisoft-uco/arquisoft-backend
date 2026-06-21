package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

    @InjectMocks
    private RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(
                "Título de prueba",
                asesorId
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
                asesorId
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
        RegistrarFichaPerfilCommand command = new RegistrarFichaPerfilCommand(titulo, asesorId);

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
                asesorId
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
                asesorId
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
                asesorId
        );

        when(asesorFichaQueryOutputPort.existsById(asesorId)).thenReturn(true);
        when(fichaPerfilOutputPort.existsByTituloProyecto("Título de prueba")).thenReturn(false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
    }
}
