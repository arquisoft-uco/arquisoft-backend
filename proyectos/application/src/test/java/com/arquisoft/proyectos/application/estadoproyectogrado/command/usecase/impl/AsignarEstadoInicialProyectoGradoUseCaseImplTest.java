package com.arquisoft.proyectos.application.estadoproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.validator.AsignarEstadoInicialProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialProyectoGradoUseCaseImplTest {

    @Mock
    private EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;

    @Mock
    private AsignarEstadoInicialProyectoGradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstadoInicialProyectoGradoUseCaseImpl useCase;

    private final EstadoProyectoGradoDomain estadoInicial =
            EstadoProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID());

    @Test
    void debeRegistrarEnProceso_cuandoElProyectoExiste() {
        // Arrange
        var proyectoGrado = estadoInicial.getProyectoGrado();
        when(proyectoGradoExisteFinder.obtener(proyectoGrado)).thenReturn(true);

        // Act
        useCase.ejecutar(estadoInicial);

        // Assert
        var orden = inOrder(proyectoGradoExisteFinder, validator, estadoProyectoGradoOutputPort, logger);
        orden.verify(proyectoGradoExisteFinder, times(1)).obtener(proyectoGrado);
        orden.verify(validator).validar(proyectoGrado, true);
        var entidad = ArgumentCaptor.forClass(EstadoProyectoGradoEntity.class);
        orden.verify(estadoProyectoGradoOutputPort).registrar(entidad.capture());
        orden.verify(logger).debug(EstadoProyectoGradoKey.LOG_CREADO,
                estadoInicial.getId(), proyectoGrado, EstadoProyecto.EN_PROCESO.getNombre());

        assertThat(entidad.getValue().id()).isEqualTo(estadoInicial.getId());
        assertThat(entidad.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(entidad.getValue().estadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO.getId());
        assertThat(entidad.getValue().fechaActualizacion()).isEqualTo(estadoInicial.getFechaActualizacion());
    }
}
