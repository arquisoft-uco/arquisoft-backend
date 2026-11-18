package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.ModificarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.ModificarItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ModificacionItemCuantitativoAsesorDomain;
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
class ModificarItemCuantitativoAsesorInteractorImplTest {

    @Mock
    private ModificarItemCuantitativoAsesorUseCase useCase;

    @InjectMocks
    private ModificarItemCuantitativoAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = ModificarItemCuantitativoAsesorCommand.crear(item, "Descripción ampliada");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionItemCuantitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCuantitativoAsesor()).isEqualTo(item);
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Descripción ampliada");
    }
}
