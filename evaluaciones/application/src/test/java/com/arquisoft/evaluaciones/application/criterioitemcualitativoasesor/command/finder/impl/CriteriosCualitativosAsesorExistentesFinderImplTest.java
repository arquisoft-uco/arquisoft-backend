package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.secondaryport.CriterioItemCualitativoAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriteriosCualitativosAsesorExistentesFinderImplTest {

    @Mock
    private CriterioItemCualitativoAsesorOutputPort outputPort;

    @InjectMocks
    private CriteriosCualitativosAsesorExistentesFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaIdsExistentes() {
        // Arrange
        var criterio = UtilUUID.generarNuevoUUID();
        var solicitados = Set.of(criterio, UtilUUID.generarNuevoUUID());
        when(outputPort.consultarIdsExistentes(solicitados)).thenReturn(Set.of(criterio));

        // Act
        var resultado = finder.obtener(solicitados);

        // Assert
        assertThat(resultado).containsExactly(criterio);
        verify(outputPort).consultarIdsExistentes(solicitados);
    }
}
