package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ArtefactosParaPaqueteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.artefactos.application.paqueteentregable.command.validator.GenerarPaqueteEntregableValidator;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.event.PaqueteEntregableGeneradoEvent;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoExcluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoIncluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactosDelPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.MotivoExclusionPaquete;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarPaqueteEntregableUseCaseImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Mock
    private AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;

    @Mock
    private PaqueteEntregableExisteFinder paqueteEntregableExisteFinder;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private ArtefactosParaPaqueteFinder artefactosParaPaqueteFinder;

    @Mock
    private GenerarPaqueteEntregableValidator generarPaqueteEntregableValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarPaqueteEntregableUseCaseImpl useCase;

    private static GeneracionPaqueteEntregableDomain generacion() {
        return GeneracionPaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2);
    }

    @Test
    void debeDevolverDuplicadoSinConsultarMas_cuandoYaExistePaquete() {
        // Arrange
        var generacion = generacion();
        when(paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        assertThat(resultado).isEqualTo(
                new GeneracionPaqueteEntregableResult.Duplicado(generacion.getEntregableProyectoGrado()));
        verifyNoInteractions(proyectoGradoPorIdFinder, artefactosParaPaqueteFinder, generarPaqueteEntregableValidator,
                almacenamientoPaqueteOutputPort, eventPublisher);
        verify(paqueteEntregableOutputPort, never()).registrar(any());
    }

    @Test
    void debeArmarRegistrarYPublicarEnOrden_cuandoTodoEsValido() {
        // Arrange
        var generacion = generacion();
        var proyecto = ProyectoGradoDomain.reconstruir(generacion.getProyectoGrado(), "Proyecto", null, null);
        var incluido = new ArtefactoIncluidoPaquete(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Doc", 1, 1, Instant.now(), "uri",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");
        var excluido = new ArtefactoExcluidoPaquete(
                UtilUUID.generarNuevoUUID(), "Otro", MotivoExclusionPaquete.SIN_VERSIONES, 0);
        var artefactos = new ArtefactosDelPaquete(List.of(incluido), List.of(excluido));
        var archivo = new ArchivoPaqueteArmado("bucket", "paquetes/x.zip", "proyecto.zip", 500L, List.of());
        when(paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado())).thenReturn(false);
        when(proyectoGradoPorIdFinder.obtener(generacion.getProyectoGrado())).thenReturn(proyecto);
        when(artefactosParaPaqueteFinder.obtener(generacion.getProyectoGrado())).thenReturn(artefactos);
        when(almacenamientoPaqueteOutputPort.armar(any())).thenReturn(archivo);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        var contenido = ArgumentCaptor.forClass(ContenidoPaqueteEntity.class);
        var entity = ArgumentCaptor.forClass(PaqueteEntregableEntity.class);
        var evento = ArgumentCaptor.forClass(PaqueteEntregableGeneradoEvent.class);
        var orden = inOrder(paqueteEntregableExisteFinder, proyectoGradoPorIdFinder, artefactosParaPaqueteFinder,
                generarPaqueteEntregableValidator, almacenamientoPaqueteOutputPort, paqueteEntregableOutputPort,
                eventPublisher);
        orden.verify(paqueteEntregableExisteFinder, times(1)).obtener(generacion.getEntregableProyectoGrado());
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(generacion.getProyectoGrado());
        orden.verify(artefactosParaPaqueteFinder, times(1)).obtener(generacion.getProyectoGrado());
        orden.verify(generarPaqueteEntregableValidator)
                .validar(new ExistenciaProyectoGrado(generacion.getProyectoGrado(), true));
        orden.verify(almacenamientoPaqueteOutputPort).armar(contenido.capture());
        orden.verify(paqueteEntregableOutputPort).registrar(entity.capture());
        orden.verify(eventPublisher).publish(evento.capture());
        assertThat(contenido.getValue().nombreProyecto()).isEqualTo("Proyecto");
        assertThat(contenido.getValue().versionEntregable()).isEqualTo(2);
        assertThat(contenido.getValue().generadoEn()).isEqualTo(generacion.getGeneradoEn());
        assertThat(contenido.getValue().incluidos()).hasSize(1);
        assertThat(contenido.getValue().excluidos()).singleElement()
                .satisfies(e -> assertThat(e.motivo()).isEqualTo("SIN_VERSIONES"));
        assertThat(entity.getValue().claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(entity.getValue().tamanoBytes()).isEqualTo(500L);
        assertThat(evento.getValue().getPaqueteEntregable()).isEqualTo(entity.getValue().id());
        assertThat(evento.getValue().getEntregableProyectoGrado()).isEqualTo(generacion.getEntregableProyectoGrado());
        assertThat(evento.getValue().getBucket()).isEqualTo("bucket");
        assertThat(evento.getValue().getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(evento.getValue().getNombreArchivo()).isEqualTo("proyecto.zip");
        assertThat(evento.getValue().getTamanoBytes()).isEqualTo(500L);
        assertThat(evento.getValue().getGeneradoEn()).isEqualTo(generacion.getGeneradoEn());
        assertThat(resultado).isEqualTo(new GeneracionPaqueteEntregableResult.Generado(
                entity.getValue().id(), generacion.getEntregableProyectoGrado(), 500L));
    }

    @Test
    void debePublicarEventoConArtefactosYRutas_cuandoSeGeneraElPaquete() {
        // Arrange
        var generacion = generacion();
        var proyecto = ProyectoGradoDomain.reconstruir(generacion.getProyectoGrado(), "Proyecto", null, null);
        var incluido = new ArtefactoIncluidoPaquete(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Doc", 1, 1, Instant.now(), "uri",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");
        var archivo = new ArchivoPaqueteArmado("bucket", "paquetes/x.zip", "proyecto.zip", 500L,
                List.of(new EntradaPaqueteArmada(incluido.artefacto(), "01-Doc/v1-doc.pdf")));
        when(paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado())).thenReturn(false);
        when(proyectoGradoPorIdFinder.obtener(generacion.getProyectoGrado())).thenReturn(proyecto);
        when(artefactosParaPaqueteFinder.obtener(generacion.getProyectoGrado()))
                .thenReturn(new ArtefactosDelPaquete(List.of(incluido), List.of()));
        when(almacenamientoPaqueteOutputPort.armar(any())).thenReturn(archivo);

        // Act
        useCase.ejecutar(generacion);

        // Assert
        var evento = ArgumentCaptor.forClass(PaqueteEntregableGeneradoEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue().getArtefactos()).singleElement().satisfies(item -> {
            assertThat(item.artefacto()).isEqualTo(incluido.artefacto());
            assertThat(item.numeroVersion()).isEqualTo(incluido.numeroVersion());
            assertThat(item.versionRepositorioArtefacto()).isEqualTo(incluido.versionRepositorioArtefacto());
            assertThat(item.nombreDocumento()).isEqualTo("Doc");
            assertThat(item.repositorio()).isEqualTo(incluido.repositorio());
            assertThat(item.nombreRepositorio()).isEqualTo("Repo");
            assertThat(item.rutaEnPaquete()).isEqualTo("01-Doc/v1-doc.pdf");
        });
    }

    @Test
    void noDebeArmarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var generacion = generacion();
        when(paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado())).thenReturn(false);
        when(proyectoGradoPorIdFinder.obtener(generacion.getProyectoGrado())).thenReturn(ProyectoGradoDomain.VACIO);
        when(artefactosParaPaqueteFinder.obtener(generacion.getProyectoGrado()))
                .thenReturn(new ArtefactosDelPaquete(List.of(), List.of()));
        doThrow(new IllegalStateException("rechazado")).when(generarPaqueteEntregableValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(generacion)).isInstanceOf(IllegalStateException.class);
        verify(almacenamientoPaqueteOutputPort, never()).armar(any());
        verify(paqueteEntregableOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeArmarIgual_cuandoNoHayArtefactosIncluidos() {
        // Arrange
        var generacion = generacion();
        var proyecto = ProyectoGradoDomain.reconstruir(generacion.getProyectoGrado(), "Proyecto", null, null);
        when(paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado())).thenReturn(false);
        when(proyectoGradoPorIdFinder.obtener(generacion.getProyectoGrado())).thenReturn(proyecto);
        when(artefactosParaPaqueteFinder.obtener(generacion.getProyectoGrado()))
                .thenReturn(new ArtefactosDelPaquete(List.of(), List.of()));
        when(almacenamientoPaqueteOutputPort.armar(any()))
                .thenReturn(new ArchivoPaqueteArmado("bucket", "paquetes/x.zip", "proyecto.zip", 120L, List.of()));

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        var entity = ArgumentCaptor.forClass(PaqueteEntregableEntity.class);
        verify(paqueteEntregableOutputPort).registrar(entity.capture());
        verify(eventPublisher).publish(any(PaqueteEntregableGeneradoEvent.class));
        assertThat(resultado).isInstanceOf(GeneracionPaqueteEntregableResult.Generado.class);
    }
}
