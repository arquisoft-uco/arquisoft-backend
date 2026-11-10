package com.arquisoft.entregables.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableVigenteExisteFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private EntregableVigenteExisteFinderImpl finder;

    @Test
    void debeExcluirLosCancelados_cuandoConsultaSiHayEntregableVigente() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.existeVigentePorProyectoGrado(
                proyectoGrado, EstadoEntregable.CANCELADO.getId())).thenReturn(true);

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado).isTrue();
    }
}
