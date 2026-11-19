package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsYResumenesJuradoFindersImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @Test
    void debeMapearLosItemsCualitativosYCuantitativosADomain_cuandoSeConsultaLaEvaluacionJurado() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        when(outputPort.consultarCualitativosCalificados(evaluacionJurado))
                .thenReturn(List.of(new ItemCualitativoCalificadoEntity(3, "Cumple")));
        when(outputPort.consultarCuantitativosCalificados(evaluacionJurado))
                .thenReturn(List.of(new ItemCuantitativoCalificadoEntity(80, 100)));

        // Act
        var cualitativos = new ItemsCualitativosCalificadosJuradoFinderImpl(outputPort).obtener(evaluacionJurado);
        var cuantitativos = new ItemsCuantitativosCalificadosJuradoFinderImpl(outputPort).obtener(evaluacionJurado);

        // Assert
        assertThat(cualitativos).containsExactly(new ItemCualitativoCalificado(3, "Cumple"));
        assertThat(cuantitativos).containsExactly(new ItemCuantitativoCalificado(80, 100));
    }

    @Test
    void debeMapearLosResumenesYTratarLaNotaNulaComoVacio_cuandoSeConsultaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.consultarResumenesPorEvaluacion(evaluacion)).thenReturn(List.of(
                new ResumenEvaluacionEntity(BigDecimal.ONE, BigDecimal.TWO, new BigDecimal("3.00")),
                new ResumenEvaluacionEntity(null, null, null)));

        // Act
        var resumenes = new ResumenesEvaluacionJuradosFinderImpl(outputPort).obtener(evaluacion);

        // Assert
        assertThat(resumenes).hasSize(2);
        assertThat(resumenes.get(0).getNotaAcumulada()).isEqualByComparingTo("3.00");
        assertThat(resumenes.get(1)).isSameAs(ResumenEvaluacionDomain.VACIO);
    }
}
