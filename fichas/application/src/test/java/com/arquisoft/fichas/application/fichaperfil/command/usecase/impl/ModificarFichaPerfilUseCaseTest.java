package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.mapper.ModificarFichaPerfilMapper;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class ModificarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

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

    @Test
    void debeModificarTitulo_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloNuevo = "Titulo nuevo";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloNuevo);

        // Act
        modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command));

        // Assert
        verify(fichaPerfilOutputPort, times(1)).actualizarTitulo(fichaId, tituloNuevo);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, "Titulo");

        doThrow(new FichaNoPropietarioException(fichaId, estudianteId))
                .when(modificarFichaPerfilValidator).validar(any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(ex).isInstanceOf(FichaNoPropietarioException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO);
        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloDuplicado() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tituloDuplicado = "Titulo duplicado";
        var command = new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloDuplicado);

        doThrow(new FichaTituloDuplicadoException(tituloDuplicado))
                .when(modificarFichaPerfilValidator).validar(any());

        // Act
        Throwable ex = catchThrowable(() -> modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(ex).isInstanceOf(FichaTituloDuplicadoException.class);
        assertThat(((BaseException) ex).getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO);
        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any(), any());
    }
}
