package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.ModificarItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ModificacionItemCualitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarItemCualitativoAsesorInteractorImplTest {

    @Mock
    private ModificarItemCualitativoAsesorUseCase useCase;

    @InjectMocks
    private ModificarItemCualitativoAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = ModificarItemCualitativoAsesorCommand.crear(item, "Descripción ampliada");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionItemCualitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCualitativoAsesor()).isEqualTo(item);
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Descripción ampliada");
    }
}
