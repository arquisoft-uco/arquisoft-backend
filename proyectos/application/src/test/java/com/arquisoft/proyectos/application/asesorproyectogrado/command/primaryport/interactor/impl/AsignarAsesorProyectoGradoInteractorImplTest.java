package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.AsignarAsesorProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AsignarAsesorProyectoGradoInteractorImplTest {

    @Mock
    private AsignarAsesorProyectoGradoUseCase useCase;

    @InjectMocks
    private AsignarAsesorProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = new AsignarAsesorProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var idVinculo = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any())).thenReturn(idVinculo);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AsignacionAsesorProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(captor.getValue().getCoordinador()).isEqualTo(command.coordinador());
        assertThat(captor.getValue().getAsesor()).isEqualTo(command.asesor());
        assertThat(resultado).isEqualTo(idVinculo);
    }
}
