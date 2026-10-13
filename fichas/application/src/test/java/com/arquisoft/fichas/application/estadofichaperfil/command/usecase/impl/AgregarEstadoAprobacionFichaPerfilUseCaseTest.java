package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorDeFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoAprobacionFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.IntegrantesVigentesDeFichaFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.ResumenEvaluacionesFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.event.FichaPerfilAprobadaEvent;
import com.arquisoft.fichas.domain.estadofichaperfil.event.FichaPerfilNoAprobadaEvent;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoAprobacionFichaPerfilUseCaseTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private IntegrantesVigentesDeFichaFinder integrantesVigentesDeFichaFinder;

    @Mock
    private ResumenEvaluacionesFichaFinder resumenEvaluacionesFichaFinder;

    @Mock
    private AsesorDeFichaFinder asesorDeFichaFinder;

    @Mock
    private AgregarEstadoAprobacionFichaPerfilValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarEstadoAprobacionFichaPerfilUseCaseImpl useCase;

    private final UUID asesorFichaId = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(
            UUID.randomUUID(), "Sistema de gestión", asesorFichaId);
    private final UUID coordinador = UUID.randomUUID();
    private final AsesorFichaDomain asesor = AsesorFichaDomain.reconstruir(asesorFichaId, "1020",
            "Carlos Ruiz", "carlos.ruiz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final EstadoFichaPerfilDomain estadoActual = EstadoFichaPerfilDomain.reconstruir(
            UUID.randomUUID(), ficha.getId(), EstadoFicha.DISPONIBLE_PARA_EVALUACION, Instant.now());
    private final List<IntegranteFicha> integrantes = List.of(
            integrante("Ana Gomez", "ana.gomez@soyuco.edu.co"),
            integrante("Luis Diaz", "luis.diaz@soyuco.edu.co"),
            integrante("Eva Ruiz", "eva.ruiz@soyuco.edu.co"));

    private static IntegranteFicha integrante(String nombre, String email) {
        return new IntegranteFicha(UUID.randomUUID(), new ContactoEstudiante(nombre, email));
    }

    private DecisionFichaPerfilDomain decision(boolean acepta) {
        return DecisionFichaPerfilDomain.crear(ficha.getId(), acepta, coordinador);
    }

    private ResumenEvaluacionesFicha resumen(long conObservaciones) {
        return new ResumenEvaluacionesFicha(ficha.getId(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 2, conObservaciones)));
    }

    private void stubFinders(ResumenEvaluacionesFicha resumen) {
        when(fichaPerfilFinder.obtener(ficha.getId())).thenReturn(ficha);
        when(estadoActualFichaPerfilFinder.obtener(ficha.getId())).thenReturn(estadoActual);
        when(integrantesVigentesDeFichaFinder.obtener(ficha.getId())).thenReturn(integrantes);
        when(resumenEvaluacionesFichaFinder.obtener(ficha.getId())).thenReturn(resumen);
        when(asesorDeFichaFinder.obtener(ficha.getId())).thenReturn(asesor);
    }

    private EstadoFichaPerfilEntity estadoPersistido() {
        var captor = ArgumentCaptor.forClass(EstadoFichaPerfilEntity.class);
        verify(estadoFichaPerfilOutputPort).agregarEstado(captor.capture());
        return captor.getValue();
    }

    private DomainEvent eventoPublicado() {
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(captor.capture());
        return captor.getValue();
    }

    @Test
    void debePersistirAprobadaYPublicarElEventoAprobado_cuandoAceptaSinObservaciones() {
        // Arrange
        var decision = decision(true);
        var resumen = resumen(0);
        stubFinders(resumen);

        // Act
        var id = useCase.ejecutar(decision);

        // Assert
        var estado = estadoPersistido();
        assertThat(estado.id()).isEqualTo(id);
        assertThat(estado.fichaPerfilId()).isEqualTo(ficha.getId());
        assertThat(estado.estadoFicha()).isEqualTo(EstadoFicha.APROBADA.getId());

        assertThat(eventoPublicado()).isInstanceOfSatisfying(FichaPerfilAprobadaEvent.class, evento -> {
            assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
            assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(evento.getEstadoFicha()).isEqualTo(EstadoFicha.APROBADA.getId());
            assertThat(evento.getCoordinadorId()).isEqualTo(coordinador);
            assertThat(evento.getAsesor())
                    .isEqualTo(new ContactoAsesor("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"));
            assertThat(evento.getEstudiantes()).containsExactlyElementsOf(integrantes);
        });

        var orden = inOrder(fichaPerfilFinder, estadoActualFichaPerfilFinder, integrantesVigentesDeFichaFinder,
                resumenEvaluacionesFichaFinder, asesorDeFichaFinder, validator, estadoFichaPerfilOutputPort,
                eventPublisher);
        orden.verify(fichaPerfilFinder, times(1)).obtener(ficha.getId());
        orden.verify(estadoActualFichaPerfilFinder, times(1)).obtener(ficha.getId());
        orden.verify(integrantesVigentesDeFichaFinder, times(1)).obtener(ficha.getId());
        orden.verify(resumenEvaluacionesFichaFinder, times(1)).obtener(ficha.getId());
        orden.verify(asesorDeFichaFinder, times(1)).obtener(ficha.getId());
        orden.verify(validator).validar(decision, ficha, asesor, estadoActual, resumen, integrantes);
        orden.verify(estadoFichaPerfilOutputPort).agregarEstado(any());
        orden.verify(eventPublisher).publish(any());
        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(ficha.getId()), eq(EstadoFicha.APROBADA));
    }

    @Test
    void debePersistirYPublicarAprobadaConObservaciones_cuandoAceptaYHayObservacionesVigentes() {
        // Arrange
        stubFinders(resumen(1));

        // Act
        useCase.ejecutar(decision(true));

        // Assert
        assertThat(estadoPersistido().estadoFicha()).isEqualTo(EstadoFicha.APROBADA_CON_OBSERVACIONES.getId());
        assertThat(eventoPublicado()).isInstanceOfSatisfying(FichaPerfilAprobadaEvent.class, evento ->
                assertThat(evento.getEstadoFicha()).isEqualTo(EstadoFicha.APROBADA_CON_OBSERVACIONES.getId()));
    }

    @Test
    void debePublicarSoloElEventoNoAprobadoConLosContactos_cuandoNoAcepta() {
        // Arrange
        stubFinders(resumen(0));

        // Act
        useCase.ejecutar(decision(false));

        // Assert
        assertThat(estadoPersistido().estadoFicha()).isEqualTo(EstadoFicha.NO_APROBADA.getId());
        assertThat(eventoPublicado()).isInstanceOfSatisfying(FichaPerfilNoAprobadaEvent.class, evento -> {
            assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
            assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(evento.getAsesor())
                    .isEqualTo(new ContactoAsesor("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"));
            assertThat(evento.getEstudiantes())
                    .containsExactlyElementsOf(integrantes.stream().map(IntegranteFicha::contacto).toList());
        });
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoUnaReglaRechazaLaDecision() {
        // Arrange
        var decision = decision(true);
        stubFinders(resumen(0));
        doThrow(new FichaPerfilNoDisponibleParaEvaluacionException(EstadoFicha.APROBADA))
                .when(validator).validar(eq(decision), any(), any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(decision))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
        verify(estadoFichaPerfilOutputPort, never()).agregarEstado(any());
        verify(eventPublisher, never()).publish(any());
    }
}
