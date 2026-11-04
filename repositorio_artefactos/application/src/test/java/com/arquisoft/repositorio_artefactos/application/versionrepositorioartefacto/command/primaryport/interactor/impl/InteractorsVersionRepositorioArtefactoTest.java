package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SolicitarCargaVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SubirVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SolicitarCargaVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SubirVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractorsVersionRepositorioArtefactoTest {

    @Mock
    private SolicitarCargaVersionRepositorioArtefactoUseCase solicitarUseCase;
    @Mock
    private SubirVersionRepositorioArtefactoUseCase subirUseCase;

    @Test
    void solicitarDebeDelegarConElDomainDelMapper() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var command = SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                repositorio, solicitante, "formato.pdf", "application/pdf", 2048L);
        var esperado = new CargaVersionRepositorioArtefactoResult(
                UtilUUID.generarNuevoUUID(), "http://minio/b", Map.of(), Instant.now());
        when(solicitarUseCase.ejecutar(any(SolicitudCargaVersionRepositorioArtefactoDomain.class)))
                .thenReturn(esperado);

        // Act
        var resultado = new SolicitarCargaVersionRepositorioArtefactoInteractorImpl(solicitarUseCase)
                .ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(SolicitudCargaVersionRepositorioArtefactoDomain.class);
        verify(solicitarUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRepositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().getSolicitante()).isEqualTo(solicitante);
        assertThat(captor.getValue().getNombreArchivo()).isEqualTo("formato.pdf");
    }

    @Test
    void subirDebeDelegarConElDomainDelMapper() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var command = SubirVersionRepositorioArtefactoCommand.crear(
                carga.toString(), repositorio, solicitante, "Formato A", true);
        var esperado = new VersionRepositorioArtefactoSubidaResult(UtilUUID.generarNuevoUUID(), 1);
        when(subirUseCase.ejecutar(any(SubidaVersionRepositorioArtefactoDomain.class))).thenReturn(esperado);

        // Act
        var resultado = new SubirVersionRepositorioArtefactoInteractorImpl(subirUseCase).ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(SubidaVersionRepositorioArtefactoDomain.class);
        verify(subirUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getCarga()).isEqualTo(carga);
        assertThat(captor.getValue().getNombreDocumento()).isEqualTo("Formato A");
        assertThat(captor.getValue().isNuevoDocumento()).isTrue();
    }
}
