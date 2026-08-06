package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesFichaPerfilUseCaseTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private AsignarEstudiantesFichaPerfilUseCaseImpl useCase;

    @Test
    void debeAsignarEstudiantes_cuandoLasReglasPasan() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(fichaPerfilId, List.of(UUID.randomUUID(), UUID.randomUUID()));

        // Act
        useCase.ejecutar(relaciones);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(2)).vincularEstudiante(any());
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        List<UUID> estudiantes = List.of(UUID.randomUUID());
        List<EstudianteFichaPerfilDomain> relaciones = EstudianteFichaPerfilDomain.crear(fichaPerfilId, estudiantes);

        // Act
        useCase.ejecutar(relaciones);

        // Assert
        verify(asignarEstudiantesFichaPerfilValidator)
                .validar(eq(fichaPerfilId), eq(estudiantes), eq(relaciones));
    }

    @Test
    void debePropagarFichaNoEncontrada_cuandoElValidadorFalla() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(fichaPerfilId, List.of(UUID.randomUUID()));

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(relaciones));

        // Assert
        assertThat(ex)
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfilId.toString());
        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debePropagarEstudianteNoEncontrado_cuandoElValidadorFalla() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(UUID.randomUUID(), List.of(estudiante));

        doThrow(new EstudianteNoEncontradoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(relaciones));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(estudiante.toString());
        assertThat(((EstudianteNoEncontradoException) ex).getErrorCode())
                .isEqualTo(FichasCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO);
        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debePropagarEstudianteDuplicado_cuandoElValidadorFalla() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        List<EstudianteFichaPerfilDomain> relaciones =
                EstudianteFichaPerfilDomain.crear(UUID.randomUUID(), List.of(estudiante));

        doThrow(new EstudianteDuplicadoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(relaciones));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudiante.toString());
        assertThat(((EstudianteDuplicadoException) ex).getErrorCode())
                .isEqualTo(FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO);
        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debePropagarLimiteExcedido_cuandoElValidadorFalla() {
        // Arrange
        List<EstudianteFichaPerfilDomain> relaciones = EstudianteFichaPerfilDomain.crear(
                UUID.randomUUID(), List.of(UUID.randomUUID(), UUID.randomUUID()));

        doThrow(new CupoEstudiantesExcedidoException(FichasLimits.FichaPerfil.ESTUDIANTES_MAX))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(relaciones));

        // Assert
        assertThat(ex)
                .isInstanceOf(CupoEstudiantesExcedidoException.class)
                .hasMessage(Messages.formatear(EstudianteFichaPerfilKey.ERROR_LIMITE_EXCEDIDO,
                        FichasLimits.FichaPerfil.ESTUDIANTES_MAX));
        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }
}
