package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.mapper.RegistrarEvaluacionFichaPerfilMapper;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionFichaPerfilUseCaseTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;


    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private RegistrarEvaluacionFichaPerfilUseCaseImpl useCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        // Act
        UUID resultado = useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command));

        // Assert
        assertThat(resultado).isNotNull();
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(evaluacionFichaPerfilOutputPort).registrarEvaluacion(any(EvaluacionFichaPerfilDomain.class));
        verify(estadoEvaluacionFichaOutputPort).registrarEstadoInicial(any(EstadoEvaluacionFichaDomain.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new FichaPerfilNoEncontradaException(fichaId))
                .when(registrarEvaluacionFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new RepresentanteComiteNoEncontradoException(representanteId))
                .when(registrarEvaluacionFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionDuplicada() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new EvaluacionFichaPerfilDuplicadaException(representanteId, fichaId))
                .when(registrarEvaluacionFichaPerfilValidator)
                .validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new DataAccessException("Error de BD") {})
                .when(evaluacionFichaPerfilOutputPort).registrarEvaluacion(any(EvaluacionFichaPerfilDomain.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Error de BD");

        verify(evaluacionFichaPerfilOutputPort).registrarEvaluacion(any(EvaluacionFichaPerfilDomain.class));
    }

    @Test
    void debeConstruirAgregadoAntesDeConsultarLaBaseDeDatos_cuandoRepresentanteEsNull() {
        // Arrange
        var command = new RegistrarEvaluacionFichaPerfilCommand(null, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(DomainValidationException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debeCrearEstadoInicialAutomatico_cuandoRegistrarEvaluacion() {
        // Arrange
        var command = new RegistrarEvaluacionFichaPerfilCommand(UUID.randomUUID(), UUID.randomUUID());

        // Act
        useCase.ejecutar(RegistrarEvaluacionFichaPerfilMapper.toDomain(command));

        // Assert
        verify(estadoEvaluacionFichaOutputPort).registrarEstadoInicial(any(EstadoEvaluacionFichaDomain.class));
    }
}
