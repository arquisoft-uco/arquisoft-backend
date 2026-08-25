package com.arquisoft.usuarios.application.representantecomitecurriculum.command.usecase.impl;

import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.RepresentanteComiteCurriculumExisteFinder;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.UsuarioExisteFinder;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.RepresentanteComiteCurriculumOutputPort;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator.AgregarRepresentanteComiteCurriculumValidator;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.RepresentanteComiteCurriculumDomain;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.event.RepresentanteComiteCurriculumAgregadoEvent;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioYaEsRepresentanteException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarRepresentanteComiteCurriculumUseCaseImplTest {

    @Mock
    private AgregarRepresentanteComiteCurriculumValidator validator;

    @Mock
    private UsuarioExisteFinder usuarioExisteFinder;

    @Mock
    private RepresentanteComiteCurriculumExisteFinder representanteComiteExisteFinder;

    @Mock
    private RepresentanteComiteCurriculumOutputPort outputPort;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AgregarRepresentanteComiteCurriculumUseCaseImpl useCase;

    @Test
    void debeAgregarRepresentante_cuandoDatosValidos() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        var representante = RepresentanteComiteCurriculumDomain.crear(usuarioId);

        when(usuarioExisteFinder.obtener(usuarioId)).thenReturn(true);
        when(representanteComiteExisteFinder.obtener(usuarioId)).thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(representante);

        // Assert
        assertThat(resultado).isEqualTo(usuarioId);
        verify(validator, times(1)).validar(usuarioId, true, false);
        verify(outputPort, times(1)).agregar(any());
        verify(eventPublisher, times(1)).publish(any(RepresentanteComiteCurriculumAgregadoEvent.class));
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioNoExiste() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        var representante = RepresentanteComiteCurriculumDomain.crear(usuarioId);

        when(usuarioExisteFinder.obtener(usuarioId)).thenReturn(false);
        when(representanteComiteExisteFinder.obtener(usuarioId)).thenReturn(false);
        doThrow(new UsuarioNoEncontradoException(usuarioId))
                .when(validator).validar(usuarioId, false, false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.ejecutar(representante))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioYaEsRepresentante_cuandoYaEsRepresentante() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        var representante = RepresentanteComiteCurriculumDomain.crear(usuarioId);

        when(usuarioExisteFinder.obtener(usuarioId)).thenReturn(true);
        when(representanteComiteExisteFinder.obtener(usuarioId)).thenReturn(true);
        doThrow(new UsuarioYaEsRepresentanteException(usuarioId))
                .when(validator).validar(usuarioId, true, true);

        // Act / Assert
        assertThatThrownBy(() -> useCase.ejecutar(representante))
                .isInstanceOf(UsuarioYaEsRepresentanteException.class);
    }

    @Test
    void debePublicarEvento_cuandoRepresentanteAgregado() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        var representante = RepresentanteComiteCurriculumDomain.crear(usuarioId);

        when(usuarioExisteFinder.obtener(usuarioId)).thenReturn(true);
        when(representanteComiteExisteFinder.obtener(usuarioId)).thenReturn(false);

        // Act
        useCase.ejecutar(representante);

        // Assert
        verify(eventPublisher, times(1)).publish(any(RepresentanteComiteCurriculumAgregadoEvent.class));
    }
}
