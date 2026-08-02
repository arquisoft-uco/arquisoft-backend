package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarFichaPerfilUseCaseTest {

    private static final UUID ASESOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private ModificarFichaPerfilValidator modificarFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarFichaPerfilUseCaseImpl modificarFichaPerfilUseCase;

    private static FichaPerfilAggregate crearFicha(UUID id, String titulo) {
        return FichaPerfilAggregate.reconstruir(id, titulo, ASESOR_ID);
    }

    @Test
    void debeModificarTitulo_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));

        // Act
        modificarFichaPerfilUseCase.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilAggregate> captor = ArgumentCaptor.forClass(FichaPerfilAggregate.class);
        verify(fichaPerfilOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().getTituloProyecto()).isEqualTo(tituloNuevo);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        doThrow(new FichaNoPropietarioException(fichaId, estudianteId))
                .when(modificarFichaPerfilValidator).validarPropiedad(any(), any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(command));

        // Assert
        assertThat(ex).isInstanceOf(FichaNoPropietarioException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO);
        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.empty());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(command));

        // Assert
        assertThat(ex).isInstanceOf(FichaNoEncontradaException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA);
        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloDuplicado = "Titulo duplicado";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloDuplicado);
        FichaPerfilAggregate ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        doThrow(new FichaTituloDuplicadoException(tituloDuplicado))
                .when(modificarFichaPerfilValidator).validarTitulo(any(), any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(command));

        // Assert
        assertThat(ex).isInstanceOf(FichaTituloDuplicadoException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO);
        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePermitirMismoTitulo_cuandoTituloNoCambia() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String mismoTitulo = "Titulo sin cambios";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, mismoTitulo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, mismoTitulo);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));

        // Act
        modificarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
        verify(fichaPerfilOutputPort, never()).existePorTituloProyecto(any());
    }

    @Test
    void debeInvocarGuardar_cuandoModificacionExitosa() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));

        // Act
        modificarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
    }
}
