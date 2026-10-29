package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
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
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;

    @Mock
    private AsesorVigenteFinder asesorVigenteFinder;

    @Mock
    private UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;

    @Mock
    private GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;

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
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(2);

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        var orden = inOrder(proyectoGradoExisteFinder, asesorVigenteFinder,
                ultimaVersionEntregableProyectoGradoFinder, generarEntregableProyectoGradoValidator,
                entregableProyectoGradoOutputPort);
        orden.verify(proyectoGradoExisteFinder, times(1)).obtener(proyecto);
        orden.verify(asesorVigenteFinder, times(1)).obtener(asesor);
        orden.verify(ultimaVersionEntregableProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(generarEntregableProyectoGradoValidator)
                .validar(new ExistenciaProyectoGrado(proyecto, true), new VigenciaAsesor(asesor, true));
        orden.verify(entregableProyectoGradoOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        assertThat(captor.getValue().version()).isEqualTo(3);
        assertThat(captor.getValue().estadoEntregable()).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION.getId());
        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(proyecto), eq(3));
    }

    @Test
    void debeAsignarVersionUno_cuandoElProyectoNoTieneEntregablesPrevios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(0);

        // Act
        useCase.ejecutar(GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor));

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        verify(entregableProyectoGradoOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().version()).isEqualTo(1);
    }

    @Test
    void debePropagarLaExcepcionYNoRegistrar_cuandoElValidatorRechaza() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(false);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(0);
        doThrow(new AsesorNoVigenteException(asesor)).when(generarEntregableProyectoGradoValidator)
                .validar(any(), any());
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(AsesorNoVigenteException.class);
        verify(entregableProyectoGradoOutputPort, never()).registrar(any());
        verify(logger, never()).info(eq(EntregableProyectoGradoKey.LOG_GENERADO), any(), any(), any());
    }
}
