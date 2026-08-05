package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.mapper.ModificarFichaPerfilMapper;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private ModificarFichaPerfilValidator modificarFichaPerfilValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private ModificarFichaPerfilUseCaseImpl modificarFichaPerfilUseCase;

    private static FichaPerfilDomain crearFicha(UUID id, String titulo) {
        return FichaPerfilDomain.reconstruir(id, titulo, ASESOR_ID);
    }

    @Test
    void debeModificarTitulo_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilDomain ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(ficha);

        // Act
        modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command));

        // Assert
        ArgumentCaptor<FichaPerfilDomain> captor = ArgumentCaptor.forClass(FichaPerfilDomain.class);
        verify(fichaPerfilOutputPort, times(1)).actualizarTitulo(captor.capture());
        assertThat(captor.getValue().getTituloProyecto()).isEqualTo(tituloNuevo);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        doThrow(new FichaNoPropietarioException(fichaId, estudianteId))
                .when(modificarFichaPerfilValidator).validar(any(), any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(ex).isInstanceOf(FichaNoPropietarioException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO);
        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        doThrow(new FichaPerfilNoEncontradaException(fichaId)).when(fichaPerfilFinder).obtener(fichaId);

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(ex).isInstanceOf(FichaPerfilNoEncontradaException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_NO_ENCONTRADA);
        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloDuplicado = "Titulo duplicado";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloDuplicado);
        FichaPerfilDomain ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(ficha);
        doThrow(new FichaTituloDuplicadoException(tituloDuplicado))
                .when(modificarFichaPerfilValidator).validar(any(), any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(ex).isInstanceOf(FichaTituloDuplicadoException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO);
        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any());
    }

    @Test
    void debePermitirMismoTitulo_cuandoTituloNoCambia() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String mismoTitulo = "Titulo sin cambios";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, mismoTitulo);
        FichaPerfilDomain ficha = crearFicha(fichaId, mismoTitulo);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(ficha);

        // Act
        modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command));

        // Assert
        verify(fichaPerfilOutputPort, times(1)).actualizarTitulo(any(FichaPerfilDomain.class));
        verify(fichaPerfilOutputPort, never()).existePorTituloProyecto(any());
    }

    @Test
    void debeInvocarGuardar_cuandoModificacionExitosa() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);
        FichaPerfilDomain ficha = crearFicha(fichaId, "Titulo original");

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(ficha);

        // Act
        modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command));

        // Assert
        verify(fichaPerfilOutputPort, times(1)).actualizarTitulo(any(FichaPerfilDomain.class));
    }
}
