package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.BaseException;
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
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @InjectMocks
    private ModificarFichaPerfilUseCase modificarFichaPerfilUseCase;

    private static FichaPerfilAggregate crearFicha(UUID id, String titulo) {
        return FichaPerfilAggregate.reconstruir(id, titulo, ASESOR_ID);
    }

    @Test
    void debeModificarTitulo_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(true);
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(fichaPerfilOutputPort.existsByTituloProyecto(tituloNuevo)).thenReturn(false);

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
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(false);

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
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(true);
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
        String tituloOriginal = "Titulo original";
        String tituloDuplicado = "Titulo duplicado";
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloDuplicado);
        FichaPerfilAggregate ficha = crearFicha(fichaId, tituloOriginal);

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(true);
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(fichaPerfilOutputPort.existsByTituloProyecto(tituloDuplicado)).thenReturn(true);

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
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, mismoTitulo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, mismoTitulo);

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(true);
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));

        // Act
        modificarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
        verify(fichaPerfilOutputPort, never()).existsByTituloProyecto(any());
    }

    @Test
    void debeInvocarGuardar_cuandoModificacionExitosa() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        ModificarFichaPerfilCommand command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilAggregate ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaId, estudianteId)).thenReturn(true);
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(fichaPerfilOutputPort.existsByTituloProyecto(tituloNuevo)).thenReturn(false);

        // Act
        modificarFichaPerfilUseCase.ejecutar(command);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).guardar(any(FichaPerfilAggregate.class));
    }
}
