package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RegistrarItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
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
class RegistrarItemCuantitativoAsesorInteractorImplTest {

    @Mock
    private RegistrarItemCuantitativoAsesorUseCase useCase;

    @InjectMocks
    private RegistrarItemCuantitativoAsesorInteractorImpl interactor;

    @Test
    void debeMapearDelegarYRetornarId_cuandoEjecutaCommand() {
        // Arrange
        var command = new RegistrarItemCuantitativoAsesorCommand(
                "Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);
        var id = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any(ItemCuantitativoAsesorDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ItemCuantitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getNombre()).isEqualTo(command.nombre());
        assertThat(captor.getValue().getCategoria()).isEqualTo(command.categoria());
    }
}
