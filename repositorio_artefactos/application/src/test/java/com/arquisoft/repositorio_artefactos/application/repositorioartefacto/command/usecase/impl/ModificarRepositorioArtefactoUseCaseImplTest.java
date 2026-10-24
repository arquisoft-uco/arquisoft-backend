package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreEnOtroRepositorioExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.ModificarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.event.RepositorioArtefactoModificadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
class ModificarRepositorioArtefactoUseCaseImplTest {

    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private RepositorioArtefactoPorIdFinder repositorioArtefactoPorIdFinder;
    @Mock
    private NombreEnOtroRepositorioExisteFinder nombreEnOtroRepositorioExisteFinder;
    @Mock
    private ModificarRepositorioArtefactoValidator validator;
    @Mock
    private RepositorioArtefactoOutputPort outputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private ModificarRepositorioArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ModificarRepositorioArtefactoUseCaseImpl(administradorPorIdFinder,
                repositorioArtefactoPorIdFinder, nombreEnOtroRepositorioExisteFinder, validator, outputPort,
                eventPublisher, logger);
    }

    @Test
    void debeActualizarYPublicarEnOrden_cuandoValidacionPasa() {
        // Arrange
        var repositorioId = UtilUUID.generarNuevoUUID();
        var administradorId = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                repositorioId, "Repo Nuevo", "Descripcion nueva", administradorId);
        var administrador = AdministradorDomain.crear(
                administradorId, "123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var repositorio = RepositorioArtefactoDomain.reconstruir(
                repositorioId, "Repo", "Descripcion", administradorId);
        when(administradorPorIdFinder.obtener(administradorId)).thenReturn(administrador);
        when(repositorioArtefactoPorIdFinder.obtener(repositorioId)).thenReturn(repositorio);
        when(nombreEnOtroRepositorioExisteFinder.obtener(modificacion)).thenReturn(false);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(administradorPorIdFinder, repositorioArtefactoPorIdFinder,
                nombreEnOtroRepositorioExisteFinder, validator, outputPort, eventPublisher);
        orden.verify(administradorPorIdFinder, times(1)).obtener(administradorId);
        orden.verify(repositorioArtefactoPorIdFinder, times(1)).obtener(repositorioId);
        orden.verify(nombreEnOtroRepositorioExisteFinder, times(1)).obtener(modificacion);
        orden.verify(validator).validar(modificacion, administrador, repositorio, false);
        orden.verify(outputPort).actualizarDatos(repositorioId, "Repo Nuevo", "Descripcion nueva");
        var captor = ArgumentCaptor.forClass(RepositorioArtefactoModificadoEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getRepositorioArtefacto()).isEqualTo(repositorioId);
        assertThat(captor.getValue().getNombreRepositorio()).isEqualTo("Repo Nuevo");
        verify(logger).info(any(ClaveMensaje.class), eq(repositorioId));
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoValidatorRechaza() {
        // Arrange
        var repositorioId = UtilUUID.generarNuevoUUID();
        var administradorId = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                repositorioId, "Repo Nuevo", "Descripcion nueva", administradorId);
        when(administradorPorIdFinder.obtener(administradorId)).thenReturn(AdministradorDomain.VACIO);
        when(repositorioArtefactoPorIdFinder.obtener(repositorioId)).thenReturn(RepositorioArtefactoDomain.VACIO);
        when(nombreEnOtroRepositorioExisteFinder.obtener(modificacion)).thenReturn(false);
        doThrow(new AdministradorNoVigenteException(administradorId)).when(validator).validar(
                modificacion, AdministradorDomain.VACIO, RepositorioArtefactoDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(AdministradorNoVigenteException.class);
        verify(outputPort, never()).actualizarDatos(any(), any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(repositorioId));
    }
}
