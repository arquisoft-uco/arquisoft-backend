package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.GenerarEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
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
class GenerarEntregableProyectoGradoInteractorImplTest {

    @Mock
    private GenerarEntregableProyectoGradoUseCase generarEntregableProyectoGradoUseCase;

    @InjectMocks
    private GenerarEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_yRetornarElId() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var esperado = UtilUUID.generarNuevoUUID();
        when(generarEntregableProyectoGradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var id = interactor.ejecutar(GenerarEntregableProyectoGradoCommand.crear(proyecto, asesor));

        // Assert
        assertThat(id).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(GeneracionEntregableProyectoGradoDomain.class);
        verify(generarEntregableProyectoGradoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
