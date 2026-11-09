package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableVigenteExisteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoGeneradoEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private EntregableVigenteExisteFinder entregableVigenteExisteFinder;

    @Mock
    private AsesorVigenteFinder asesorVigenteFinder;

    @Mock
    private UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;

    @Mock
    private GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarEntregableProyectoGradoUseCaseImpl useCase;

    @Test
    void debeRegistrarConVersionSiguienteYRetornarId_cuandoTodoEsValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        var proyectoDomain = ProyectoGradoDomain.crear(proyecto, "Sistema de gestion");
        when(proyectoGradoPorIdFinder.obtener(proyecto)).thenReturn(proyectoDomain);
        when(entregableVigenteExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(2);

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        var orden = inOrder(proyectoGradoPorIdFinder, asesorVigenteFinder,
                entregableVigenteExisteFinder,
                ultimaVersionEntregableProyectoGradoFinder, generarEntregableProyectoGradoValidator,
                entregableProyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto);
        orden.verify(asesorVigenteFinder, times(1)).obtener(asesor);
        orden.verify(entregableVigenteExisteFinder, times(1)).obtener(proyecto);
        orden.verify(ultimaVersionEntregableProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(generarEntregableProyectoGradoValidator)
                .validar(entrada, proyectoDomain, true, false);
        orden.verify(entregableProyectoGradoOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        assertThat(captor.getValue().version()).isEqualTo(3);
        assertThat(captor.getValue().estadoEntregable()).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION.getId());
        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(proyecto), eq(3));
    }

    @Test
    void debePublicarEventoGenerado_cuandoSeRegistra() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        var proyectoDomain = ProyectoGradoDomain.crear(proyecto, "Sistema de gestion");
        when(proyectoGradoPorIdFinder.obtener(proyecto)).thenReturn(proyectoDomain);
        when(entregableVigenteExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(0);

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoGeneradoEvent.class);
        var orden = inOrder(entregableProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(entregableProyectoGradoOutputPort).registrar(any(EntregableProyectoGradoEntity.class));
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(any(ClaveMensaje.class), eq(id), eq(proyecto), eq(1));
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVersion()).isEqualTo(1);
    }

    @Test
    void noDebePublicarEvento_cuandoElValidatorRechaza() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        when(proyectoGradoPorIdFinder.obtener(proyecto)).thenReturn(ProyectoGradoDomain.VACIO);
        when(entregableVigenteExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(0);
        doThrow(new IllegalStateException("rechazado")).when(generarEntregableProyectoGradoValidator)
                .validar(any(), any(), anyBoolean(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(IllegalStateException.class);
        verify(entregableProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), any(), eq(proyecto), anyInt());
    }
}
