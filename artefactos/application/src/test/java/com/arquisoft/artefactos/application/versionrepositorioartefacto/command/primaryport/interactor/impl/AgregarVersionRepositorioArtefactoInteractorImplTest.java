package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.AgregarVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarVersionRepositorioArtefactoInteractorImplTest {

    @Mock
    private AgregarVersionRepositorioArtefactoUseCase agregarVersionRepositorioArtefactoUseCase;

    @InjectMocks
    private AgregarVersionRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var command = AgregarVersionRepositorioArtefactoCommand.crear(
                id.toString(), 2, "Plantilla", repositorio.toString(), "Repositorio",
                new AgregarVersionRepositorioArtefactoCommand.DatosArchivo("plantilla.pdf", "application/pdf", 2048L));
        var esperado = new AgregacionVersionRepositorioArtefactoResult.Agregada(id);
        when(agregarVersionRepositorioArtefactoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(VersionRepositorioArtefactoDomain.class);
        verify(agregarVersionRepositorioArtefactoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getNombreDocumento()).isEqualTo("Plantilla");
        assertThat(captor.getValue().getVersionDocumento()).isEqualTo(2);
        assertThat(captor.getValue().getRepositorioArtefacto()).isEqualTo(repositorio);
        assertThat(captor.getValue().getNombreRepositorio()).isEqualTo("Repositorio");
        assertThat(captor.getValue().getNombreArchivo()).isEqualTo("plantilla.pdf");
        assertThat(captor.getValue().getTipoContenido()).isEqualTo("application/pdf");
        assertThat(captor.getValue().getTamanoBytes()).isEqualTo(2048L);
    }
}
