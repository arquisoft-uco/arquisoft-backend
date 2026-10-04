package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.IntegrantesVigentesDeFichaFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.ResumenEvaluacionesFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.event.EstadoFichaPerfilAgregadoEvent;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.TransicionEstadoFichaNoPermitidaException;
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
class AgregarEstadoFichaPerfilUseCaseTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private ResumenEvaluacionesFichaFinder resumenEvaluacionesFichaFinder;

    @Mock
    private IntegrantesVigentesDeFichaFinder integrantesVigentesDeFichaFinder;

    @Mock
    private AgregarEstadoFichaPerfilValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarEstadoFichaPerfilUseCaseImpl useCase;

    private final UUID asesorFicha = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(
            UUID.randomUUID(), "Sistema de gestión", asesorFicha);
    private final EstadoFichaPerfilDomain estadoActual = EstadoFichaPerfilDomain.reconstruir(
            UUID.randomUUID(), ficha.getId(), EstadoFicha.EN_CONSTRUCCION, Instant.now());
    private final ResumenEvaluacionesFicha resumen = new ResumenEvaluacionesFicha(ficha.getId(), List.of(
            new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0)));
    private final List<IntegranteFicha> integrantes = List.of(
            integrante("Ana Gomez", "ana.gomez@soyuco.edu.co"),
            integrante("Luis Diaz", "luis.diaz@soyuco.edu.co"),
            integrante("Eva Ruiz", "eva.ruiz@soyuco.edu.co"));

    private static IntegranteFicha integrante(String nombre, String email) {
        return new IntegranteFicha(UUID.randomUUID(), new ContactoEstudiante(nombre, email));
    }

    private AgregacionEstadoFichaPerfilDomain entrada() {
        return AgregacionEstadoFichaPerfilDomain.crear(
                EstadoFichaPerfilDomain.crearPorAsesor(ficha.getId(), "DISPONIBLE_PARA_EVALUACION"), asesorFicha);
    }

    private void stubFinders() {
        when(fichaPerfilFinder.obtener(ficha.getId())).thenReturn(ficha);
        when(estadoActualFichaPerfilFinder.obtener(ficha.getId())).thenReturn(estadoActual);
        when(resumenEvaluacionesFichaFinder.obtener(ficha.getId())).thenReturn(resumen);
        when(integrantesVigentesDeFichaFinder.obtener(ficha.getId())).thenReturn(integrantes);
    }

    @Test
    void debePersistirElEstadoYPublicarElEventoConLosEstudiantes_cuandoLasReglasPasan() {
        // Arrange
        var entrada = entrada();
        stubFinders();

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        assertThat(id).isEqualTo(entrada.getEstado().getId());

        var entityCaptor = ArgumentCaptor.forClass(EstadoFichaPerfilEntity.class);
        verify(estadoFichaPerfilOutputPort).agregarEstado(entityCaptor.capture());
        assertThat(entityCaptor.getValue().id()).isEqualTo(id);
        assertThat(entityCaptor.getValue().fichaPerfilId()).isEqualTo(ficha.getId());
        assertThat(entityCaptor.getValue().estadoFicha()).isEqualTo(EstadoFicha.DISPONIBLE_PARA_EVALUACION.getId());

        var eventoCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher, times(1)).publish(eventoCaptor.capture());
        assertThat(eventoCaptor.getValue()).isInstanceOfSatisfying(EstadoFichaPerfilAgregadoEvent.class, evento -> {
            assertThat(evento.getEstadoFichaPerfilId()).isEqualTo(id);
            assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
            assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(evento.getEstadoFicha()).isEqualTo(EstadoFicha.DISPONIBLE_PARA_EVALUACION.getId());
            assertThat(evento.getEstadoFichaNombre()).isEqualTo(EstadoFicha.DISPONIBLE_PARA_EVALUACION.getNombre());
            assertThat(evento.getEstudiantes()).containsExactly(
                    new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                    new ContactoEstudiante("Luis Diaz", "luis.diaz@soyuco.edu.co"),
                    new ContactoEstudiante("Eva Ruiz", "eva.ruiz@soyuco.edu.co"));
        });

        var orden = inOrder(fichaPerfilFinder, estadoActualFichaPerfilFinder, resumenEvaluacionesFichaFinder,
                integrantesVigentesDeFichaFinder, validator, estadoFichaPerfilOutputPort, eventPublisher);
        orden.verify(fichaPerfilFinder, times(1)).obtener(ficha.getId());
        orden.verify(estadoActualFichaPerfilFinder, times(1)).obtener(ficha.getId());
        orden.verify(resumenEvaluacionesFichaFinder, times(1)).obtener(ficha.getId());
        orden.verify(integrantesVigentesDeFichaFinder, times(1)).obtener(ficha.getId());
        orden.verify(validator).validar(entrada, ficha, estadoActual, resumen, integrantes);
        orden.verify(estadoFichaPerfilOutputPort).agregarEstado(any());
        orden.verify(eventPublisher).publish(any());
        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(ficha.getId()),
                eq(EstadoFicha.DISPONIBLE_PARA_EVALUACION.getId()));
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoUnaReglaRechazaElCambio() {
        // Arrange
        var entrada = entrada();
        stubFinders();
        doThrow(new TransicionEstadoFichaNoPermitidaException(
                EstadoFicha.DESCARTADA, EstadoFicha.DISPONIBLE_PARA_EVALUACION))
                .when(validator).validar(eq(entrada), any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(TransicionEstadoFichaNoPermitidaException.class);
        verify(estadoFichaPerfilOutputPort, never()).agregarEstado(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(entrada.getEstado().getId()), any(), any());
    }
}
