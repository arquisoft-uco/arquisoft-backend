package com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model.AgregarEntregableProyectoGradoCommand;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase.AgregarEntregableProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
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
class AgregarEntregableProyectoGradoInteractorImplTest {

    @Mock
    private AgregarEntregableProyectoGradoUseCase useCase;

    @InjectMocks
    private AgregarEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeadoYDevolverElResult_cuandoEjecuta() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = new AgregarEntregableProyectoGradoCommand(
                entregable, proyecto, 3, "Aprobado", "entregables", "a/b.zip", "b.zip");
        var esperado = new AgregacionEntregableProyectoGradoResult.Agregado(entregable, proyecto);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isSameAs(esperado);
        assertThat(captor.getValue().getId()).isEqualTo(entregable);
        assertThat(captor.getValue().getVersionEntregable()).isEqualTo(3);
        assertThat(captor.getValue().getNombreArchivo()).isEqualTo("b.zip");
    }
}
