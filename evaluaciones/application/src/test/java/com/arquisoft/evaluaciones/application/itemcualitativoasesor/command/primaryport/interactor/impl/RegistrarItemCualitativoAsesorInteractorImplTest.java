package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RegistrarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RegistrarItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
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
class RegistrarItemCualitativoAsesorInteractorImplTest {

    @Mock
    private RegistrarItemCualitativoAsesorUseCase useCase;

    @InjectMocks
    private RegistrarItemCualitativoAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeado() {
        // Arrange
        var command = new RegistrarItemCualitativoAsesorCommand("Claridad", "Descripción");
        var id = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any(ItemCualitativoAsesorDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ItemCualitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getNombre()).isEqualTo(command.nombre());
        assertThat(captor.getValue().getDescripcion()).isEqualTo(command.descripcion());
    }
}
