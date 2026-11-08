package com.arquisoft.entregables.application.paqueteentregable.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EstudianteParticipaEntregableQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.model.EstudianteEntregable;
import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.finder.PaqueteEntregableQueryFinder;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.EnlaceDescargaPaqueteQueryOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.query.validator.DescargarPaqueteEntregableValidator;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarPaqueteEntregableUseCaseImplTest {

    @Mock
    private AsesorDeEntregableProyectoGradoQueryFinder asesorDeEntregableProyectoGradoQueryFinder;

    @Mock
    private EstudianteParticipaEntregableQueryFinder estudianteParticipaEntregableQueryFinder;

    @Mock
    private PaqueteEntregableQueryFinder paqueteEntregableQueryFinder;

    @Mock
    private DescargarPaqueteEntregableValidator descargarPaqueteEntregableValidator;

    @Mock
    private EnlaceDescargaPaqueteQueryOutputPort enlaceDescargaPaqueteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescargarPaqueteEntregableUseCaseImpl useCase;

    @Test
    void debeGenerarEnlace_cuandoValidatorPasa() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaPaqueteEntregableCriteria(entregable, actor, false, true);
        var paquete = PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), entregable, "bucket",
                "paquetes/x.zip", "p.zip", 10L, Instant.now());
        var enlace = new EnlaceDescargaPaqueteEntregableReadModel("https://url", Instant.now());
        when(asesorDeEntregableProyectoGradoQueryFinder.obtener(entregable)).thenReturn(asesor);
        when(estudianteParticipaEntregableQueryFinder.obtener(new EstudianteEntregable(actor, entregable)))
                .thenReturn(true);
        when(paqueteEntregableQueryFinder.obtener(entregable)).thenReturn(paquete);
        when(enlaceDescargaPaqueteQueryOutputPort.generar("bucket", "paquetes/x.zip", "p.zip")).thenReturn(enlace);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        var orden = inOrder(asesorDeEntregableProyectoGradoQueryFinder, estudianteParticipaEntregableQueryFinder,
                paqueteEntregableQueryFinder, descargarPaqueteEntregableValidator,
                enlaceDescargaPaqueteQueryOutputPort);
        orden.verify(asesorDeEntregableProyectoGradoQueryFinder, times(1)).obtener(entregable);
        orden.verify(estudianteParticipaEntregableQueryFinder, times(1))
                .obtener(new EstudianteEntregable(actor, entregable));
        orden.verify(paqueteEntregableQueryFinder, times(1)).obtener(entregable);
        orden.verify(descargarPaqueteEntregableValidator).validar(criteria, asesor, true, paquete);
        orden.verify(enlaceDescargaPaqueteQueryOutputPort).generar("bucket", "paquetes/x.zip", "p.zip");
        assertThat(resultado).isSameAs(enlace);
    }

    @Test
    void noDebeGenerarEnlace_cuandoValidatorRechaza() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = new DescargaPaqueteEntregableCriteria(entregable, actor, true, false);
        when(asesorDeEntregableProyectoGradoQueryFinder.obtener(entregable))
                .thenReturn(UtilUUID.obtenerUUIDPorDefecto());
        when(estudianteParticipaEntregableQueryFinder.obtener(any())).thenReturn(false);
        when(paqueteEntregableQueryFinder.obtener(entregable)).thenReturn(PaqueteEntregableDomain.VACIO);
        doThrow(new IllegalStateException("rechazado")).when(descargarPaqueteEntregableValidator)
                .validar(any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria)).isInstanceOf(IllegalStateException.class);
        verify(enlaceDescargaPaqueteQueryOutputPort, never()).generar(any(), any(), any());
    }
}
