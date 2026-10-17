package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.secondaryport.CriterioItemCualitativoJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriteriosCualitativosJuradoExistentesFinderImplTest {

    @Mock
    private CriterioItemCualitativoJuradoOutputPort outputPort;

    @InjectMocks
    private CriteriosCualitativosJuradoExistentesFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaIdsExistentes() {
        // Arrange
        UUID criterio = UUID.randomUUID();
        Set<UUID> solicitados = Set.of(criterio);
        when(outputPort.consultarIdsExistentes(solicitados)).thenReturn(Set.of(criterio));

        // Act
        Set<UUID> resultado = finder.obtener(solicitados);

        // Assert
        assertThat(resultado).containsExactly(criterio);
        verify(outputPort).consultarIdsExistentes(solicitados);
    }
}
