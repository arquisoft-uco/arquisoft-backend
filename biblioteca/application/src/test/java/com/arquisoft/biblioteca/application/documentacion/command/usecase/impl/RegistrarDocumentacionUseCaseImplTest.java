package com.arquisoft.biblioteca.application.documentacion.command.usecase.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.AutorDocumentacionOutputPort;
import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;
import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionDelEntregableExisteFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.AlmacenamientoDocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.application.documentacion.command.validator.RegistrarDocumentacionValidator;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.AutoresDeEntregableFinder;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDocumentacionUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoBloqueadoPorIdFinder entregableFinder;
    @Mock
    private DocumentacionDelEntregableExisteFinder documentacionExisteFinder;
    @Mock
    private BibliotecarioPorIdFinder bibliotecarioFinder;
    @Mock
    private AutoresDeEntregableFinder autoresFinder;
    @Mock
    private RegistrarDocumentacionValidator validator;
    @Mock
    private DocumentacionOutputPort documentacionOutputPort;
    @Mock
    private AutorDocumentacionOutputPort autorDocumentacionOutputPort;
    @Mock
    private AlmacenamientoDocumentacionOutputPort almacenamientoOutputPort;
    @Mock
    private AppLogger logger;

    private RegistrarDocumentacionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarDocumentacionUseCaseImpl(entregableFinder, documentacionExisteFinder,
                bibliotecarioFinder, autoresFinder, validator, documentacionOutputPort,
                autorDocumentacionOutputPort, almacenamientoOutputPort, logger);
    }

    @Test
    void debeRegistrarDocumentacionYAutoresEnOrden_cuandoTodoEsValido() {
        // Arrange
        var entregableId = UtilUUID.generarNuevoUUID();
        var bibliotecarioId = UtilUUID.generarNuevoUUID();
        var registro = RegistroDocumentacionDomain.crear(entregableId, bibliotecarioId);
        var entregable = EntregableProyectoGradoDomain.reconstruir(
                entregableId, UtilUUID.generarNuevoUUID(), 1, EstadoEntregable.APROBADO, "proyectos/a.zip", "a.zip");
        var bibliotecario = BibliotecarioDomain.reconstruir(bibliotecarioId, "123", "Ana", "ana@uco.edu.co",
                Instant.parse("2026-11-15T10:00:00Z"), UtilFecha.VACIO);
        var autores = List.of(
                new AutorDeEntregable(UtilUUID.generarNuevoUUID(), true),
                new AutorDeEntregable(UtilUUID.generarNuevoUUID(), true),
                new AutorDeEntregable(UtilUUID.generarNuevoUUID(), true));
        when(entregableFinder.obtener(entregableId)).thenReturn(entregable);
        when(documentacionExisteFinder.obtener(entregableId)).thenReturn(false);
        when(bibliotecarioFinder.obtener(bibliotecarioId)).thenReturn(bibliotecario);
        when(autoresFinder.obtener(entregableId)).thenReturn(autores);
        when(almacenamientoOutputPort.bucketEntregables()).thenReturn("bucket-entregables");

        // Act
        var id = useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(entregableFinder, documentacionExisteFinder, bibliotecarioFinder, autoresFinder,
                validator, documentacionOutputPort, autorDocumentacionOutputPort, almacenamientoOutputPort, logger);
        orden.verify(logger).info(DocumentacionKey.LOG_REGISTRANDO, entregableId, bibliotecarioId);
        orden.verify(entregableFinder, times(1)).obtener(entregableId);
        orden.verify(documentacionExisteFinder, times(1)).obtener(entregableId);
        orden.verify(bibliotecarioFinder, times(1)).obtener(bibliotecarioId);
        orden.verify(autoresFinder, times(1)).obtener(entregableId);
        orden.verify(validator).validar(registro, entregable, false, bibliotecario, autores);
        var documentacionCaptor = ArgumentCaptor.forClass(DocumentacionEntity.class);
        orden.verify(documentacionOutputPort).registrar(documentacionCaptor.capture());
        var autoresCaptor = ArgumentCaptor.<List<AutorDocumentacionEntity>>captor();
        orden.verify(autorDocumentacionOutputPort).registrarTodos(autoresCaptor.capture());
        orden.verify(logger).info(eq(DocumentacionKey.LOG_REGISTRADA), eq(id), eq(entregableId), eq(3));
        var documentacion = documentacionCaptor.getValue();
        assertThat(documentacion.id()).isEqualTo(id);
        assertThat(documentacion.url()).isEqualTo("bucket-entregables/proyectos/a.zip");
        assertThat(documentacion.entregableProyectoGrado()).isEqualTo(entregableId);
        assertThat(documentacion.bibliotecario()).isEqualTo(bibliotecarioId);
        assertThat(documentacion.estadoDocumentacion()).isEqualTo(EstadoDocumentacion.PENDIENTE.getId());
        assertThat(autoresCaptor.getValue())
                .extracting(AutorDocumentacionEntity::autor)
                .containsExactlyElementsOf(autores.stream().map(AutorDeEntregable::autor).toList());
        assertThat(autoresCaptor.getValue())
                .allSatisfy(autor -> assertThat(autor.documentacion()).isEqualTo(id));
    }

    @Test
    void noDebePersistirNiCerrarElLog_cuandoElValidatorRechaza() {
        // Arrange
        var entregableId = UtilUUID.generarNuevoUUID();
        var registro = RegistroDocumentacionDomain.crear(entregableId, UtilUUID.generarNuevoUUID());
        when(entregableFinder.obtener(entregableId)).thenReturn(EntregableProyectoGradoDomain.VACIO);
        when(documentacionExisteFinder.obtener(entregableId)).thenReturn(false);
        when(bibliotecarioFinder.obtener(registro.getBibliotecario())).thenReturn(BibliotecarioDomain.VACIO);
        when(autoresFinder.obtener(entregableId)).thenReturn(List.of());
        doThrow(new EntregableProyectoGradoNoEncontradoException(entregableId))
                .when(validator).validar(any(), any(), anyBoolean(), any(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
        verify(documentacionOutputPort, never()).registrar(any());
        verify(autorDocumentacionOutputPort, never()).registrarTodos(any());
        verify(logger, never()).info(eq(DocumentacionKey.LOG_REGISTRADA), any(), any(), any());
    }
}
