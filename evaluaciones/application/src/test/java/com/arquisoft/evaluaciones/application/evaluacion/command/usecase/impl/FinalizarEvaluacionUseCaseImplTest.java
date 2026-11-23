package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.ContactoAsesorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.ContactoCoordinadorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.ContactosEstudiantesEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.AvanceEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ObservacionesEvaluacionExistenFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.PesosFinalizacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.FinalizacionEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.ConsolidarNotasEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.FinalizarEvaluacionValidator;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ResumenEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ResumenesEvaluacionJuradosFinder;
import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.DatosEntregable;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.ProgresoParticipantes;
import com.arquisoft.evaluaciones.domain.evaluacion.ConsolidacionNotasEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.VerificacionFinalizacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.event.EvaluacionFinalizadaEvent;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.ResumenesEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarEvaluacionUseCaseImplTest {

    @Mock
    private AvanceEvaluacionFinder avanceEvaluacionFinder;
    @Mock
    private ResumenEvaluacionAsesorFinder resumenEvaluacionAsesorFinder;
    @Mock
    private ResumenesEvaluacionJuradosFinder resumenesEvaluacionJuradosFinder;
    @Mock
    private PesosFinalizacionFinder pesosFinalizacionFinder;
    @Mock
    private ContactoAsesorEvaluacionFinder contactoAsesorEvaluacionFinder;
    @Mock
    private ContactoCoordinadorEvaluacionFinder contactoCoordinadorEvaluacionFinder;
    @Mock
    private ContactosEstudiantesEvaluacionFinder contactosEstudiantesEvaluacionFinder;
    @Mock
    private ObservacionesEvaluacionExistenFinder observacionesEvaluacionExistenFinder;
    @Mock
    private FinalizarEvaluacionValidator validator;
    @Mock
    private ConsolidarNotasEvaluacionUseCase consolidarNotasEvaluacionUseCase;
    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private FinalizarEvaluacionUseCaseImpl useCase;

    private static ResumenEvaluacionDomain resumen(String nota) {
        return ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, new BigDecimal(nota));
    }

    private static AvanceEvaluacionDomain avance(UUID evaluacion, EstadoEvaluacion estado, long jurados) {
        return AvanceEvaluacionDomain.reconstruir(evaluacion, estado,
                new DatosEntregable(UtilUUID.generarNuevoUUID(), "Proyecto", 2),
                new ProgresoParticipantes(jurados, jurados, true));
    }

    private void prepararFinders(UUID evaluacion, boolean observaciones) {
        when(resumenEvaluacionAsesorFinder.obtener(evaluacion)).thenReturn(resumen("4.00"));
        when(resumenesEvaluacionJuradosFinder.obtener(evaluacion))
                .thenReturn(List.of(resumen("3.00"), resumen("3.00"), resumen("3.00")));
        when(pesosFinalizacionFinder.obtener()).thenReturn(new PesosFinalizacion(BigDecimal.ONE, BigDecimal.ONE));
        when(contactoAsesorEvaluacionFinder.obtener(evaluacion))
                .thenReturn(new ContactoAsesor(UtilUUID.generarNuevoUUID(), "Asesor", "asesor@uco.edu.co"));
        when(contactoCoordinadorEvaluacionFinder.obtener(evaluacion))
                .thenReturn(new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Coord", "coord@uco.edu.co"));
        when(contactosEstudiantesEvaluacionFinder.obtener(evaluacion))
                .thenReturn(List.of(new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Est", "est@uco.edu.co")));
        when(observacionesEvaluacionExistenFinder.obtener(evaluacion)).thenReturn(observaciones);
    }

    @Test
    void debeConsolidarFinalizarYPublicarEnOrden_cuandoTodosTerminaron() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var avance = avance(evaluacion, EstadoEvaluacion.EN_PROGRESO, 3);
        when(avanceEvaluacionFinder.obtener(evaluacion)).thenReturn(avance);
        prepararFinders(evaluacion, false);

        // Act
        useCase.ejecutar(VerificacionFinalizacionEvaluacionDomain.crear(evaluacion));

        // Assert
        var orden = inOrder(avanceEvaluacionFinder, resumenEvaluacionAsesorFinder, resumenesEvaluacionJuradosFinder,
                pesosFinalizacionFinder, contactoAsesorEvaluacionFinder, contactoCoordinadorEvaluacionFinder,
                contactosEstudiantesEvaluacionFinder, observacionesEvaluacionExistenFinder, validator,
                consolidarNotasEvaluacionUseCase, evaluacionOutputPort, eventPublisher);
        orden.verify(avanceEvaluacionFinder).obtener(evaluacion);
        orden.verify(resumenEvaluacionAsesorFinder).obtener(evaluacion);
        orden.verify(resumenesEvaluacionJuradosFinder).obtener(evaluacion);
        orden.verify(pesosFinalizacionFinder).obtener();
        orden.verify(contactoAsesorEvaluacionFinder).obtener(evaluacion);
        orden.verify(contactoCoordinadorEvaluacionFinder).obtener(evaluacion);
        orden.verify(contactosEstudiantesEvaluacionFinder).obtener(evaluacion);
        orden.verify(observacionesEvaluacionExistenFinder).obtener(evaluacion);
        orden.verify(validator).validar(any(ResumenesEvaluacion.class), any(DestinatariosEvaluacionFinalizada.class));
        orden.verify(consolidarNotasEvaluacionUseCase).ejecutar(any(ConsolidacionNotasEvaluacionDomain.class));
        orden.verify(evaluacionOutputPort).finalizar(any(FinalizacionEvaluacionEntity.class));
        orden.verify(eventPublisher).publish(any(EvaluacionFinalizadaEvent.class));

        verify(resumenesEvaluacionJuradosFinder, times(1)).obtener(any());

        var resumenes = ArgumentCaptor.forClass(ResumenesEvaluacion.class);
        verify(validator).validar(resumenes.capture(), any());
        assertThat(resumenes.getValue().jurados()).hasSize(3);
        assertThat(resumenes.getValue().juradosEvaluados()).isEqualTo(3);

        var consolidacion = ArgumentCaptor.forClass(ConsolidacionNotasEvaluacionDomain.class);
        verify(consolidarNotasEvaluacionUseCase).ejecutar(consolidacion.capture());
        assertThat(consolidacion.getValue().getCantidadJurados()).isEqualTo(3);

        var finalizacion = ArgumentCaptor.forClass(FinalizacionEvaluacionEntity.class);
        verify(evaluacionOutputPort).finalizar(finalizacion.capture());
        assertThat(finalizacion.getValue().evaluacion()).isEqualTo(evaluacion);
        assertThat(finalizacion.getValue().estado()).isEqualTo("FINALIZADA");
        assertThat(finalizacion.getValue().notaFinal()).isEqualByComparingTo("3.25");
        assertThat(finalizacion.getValue().resultado()).isEqualTo("APROBADA");

        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(EvaluacionFinalizadaEvent.class, publicado -> {
            assertThat(publicado.getEvaluacionId()).isEqualTo(evaluacion);
            assertThat(publicado.getEntregableId()).isEqualTo(avance.getEntregable().id());
            assertThat(publicado.getNombreProyecto()).isEqualTo("Proyecto");
            assertThat(publicado.getVersionEntregable()).isEqualTo(2);
            assertThat(publicado.getResultado()).isEqualTo("APROBADA");
            assertThat(publicado.getCoordinador().email()).isEqualTo("coord@uco.edu.co");
            assertThat(publicado.getAsesor().email()).isEqualTo("asesor@uco.edu.co");
            assertThat(publicado.getEstudiantes()).extracting(ContactoEstudiante::email)
                    .containsExactly("est@uco.edu.co");
        });
    }

    @Test
    void debeResolverAprobadaConObservaciones_cuandoExistenObservaciones() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(avanceEvaluacionFinder.obtener(evaluacion)).thenReturn(avance(evaluacion, EstadoEvaluacion.EN_PROGRESO, 3));
        prepararFinders(evaluacion, true);

        // Act
        useCase.ejecutar(VerificacionFinalizacionEvaluacionDomain.crear(evaluacion));

        // Assert
        var finalizacion = ArgumentCaptor.forClass(FinalizacionEvaluacionEntity.class);
        verify(evaluacionOutputPort).finalizar(finalizacion.capture());
        assertThat(finalizacion.getValue().resultado()).isEqualTo("APROBADA_CON_OBSERVACIONES");
    }

    @Test
    void debeNoHacerNada_cuandoLaEvaluacionNoPuedeFinalizarse() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(avanceEvaluacionFinder.obtener(evaluacion)).thenReturn(avance(evaluacion, EstadoEvaluacion.FINALIZADA, 3));

        // Act
        useCase.ejecutar(VerificacionFinalizacionEvaluacionDomain.crear(evaluacion));

        // Assert
        verifyNoInteractions(resumenEvaluacionAsesorFinder, resumenesEvaluacionJuradosFinder, pesosFinalizacionFinder,
                contactoAsesorEvaluacionFinder, contactoCoordinadorEvaluacionFinder,
                contactosEstudiantesEvaluacionFinder, observacionesEvaluacionExistenFinder, validator,
                consolidarNotasEvaluacionUseCase, evaluacionOutputPort, eventPublisher);
    }

    @Test
    void debeAbortarSinEscribirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(avanceEvaluacionFinder.obtener(evaluacion)).thenReturn(avance(evaluacion, EstadoEvaluacion.EN_PROGRESO, 3));
        prepararFinders(evaluacion, false);
        doThrow(new ResumenesEvaluacionIncompletosException(evaluacion, EvaluacionKey.ERROR_RESUMENES_ASESOR))
                .when(validator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(VerificacionFinalizacionEvaluacionDomain.crear(evaluacion)))
                .isInstanceOf(ResumenesEvaluacionIncompletosException.class);
        verifyNoInteractions(consolidarNotasEvaluacionUseCase, evaluacionOutputPort, eventPublisher);
        verify(logger, never()).debug(eq(EvaluacionKey.LOG_FINALIZADA), any(), any(), any());
    }
}
