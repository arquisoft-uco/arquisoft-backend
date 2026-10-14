package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ModificarObservacionItemUseCase;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
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
class ModificarObservacionItemInteractorImplTest {

    @Mock
    private ModificarObservacionItemUseCase modificarObservacionItemUseCase;

    @InjectMocks
    private ModificarObservacionItemInteractorImpl modificarObservacionItemInteractor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = ModificarObservacionItemCommand.crear(observacionItem, "Observación válida", asesorFicha);

        // Act
        modificarObservacionItemInteractor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionObservacionItemDomain.class);
        verify(modificarObservacionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionItem()).isEqualTo(observacionItem);
        assertThat(captor.getValue().getObservacion()).isEqualTo("Observación válida");
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
