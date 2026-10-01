package com.arquisoft.fichas.application.tipoitem.query.usecase.impl;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.application.tipoitem.query.secondaryport.TipoItemQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.TipoItemKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposItemUseCaseImplTest {

    @Mock
    private TipoItemQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarTiposItemUseCaseImpl useCase;

    @Test
    void debeRetornarLosTiposItem_cuandoElPuertoDevuelveResultados() {
        // Arrange
        List<TipoItemReadModel> tiposEsperados = List.of(
                new TipoItemReadModel("OBJETIVO_GENERAL", "Objetivo General",
                        "Proposito principal del proyecto y su impacto esperado."),
                new TipoItemReadModel("ANTECEDENTES", "Antecedentes",
                        "Estudios/proyectos previos que contextualizan el proyecto."),
                new TipoItemReadModel("REFERENCIAS", "Referencias",
                        "Fuentes bibliograficas en formato de citacion estandar.")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(tiposEsperados);

        // Act
        List<TipoItemReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactlyElementsOf(tiposEsperados);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        List<TipoItemReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDelegarEnElPuerto_sinTransformarElResultado() {
        // Arrange
        List<TipoItemReadModel> tiposEsperados = List.of(
                new TipoItemReadModel("JUSTIFICACION", "Justificacion", "Importancia del proyecto.")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(tiposEsperados);

        // Act
        List<TipoItemReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(tiposEsperados);
        verify(queryOutputPort, times(1)).consultarTodos();
    }

    @Test
    void debeRegistrarElTotalEnLog_cuandoConsultaCompleta() {
        // Arrange
        List<TipoItemReadModel> tiposEsperados = List.of(
                new TipoItemReadModel("ESTADO_DEL_ARTE", "Estado Del Arte", "Revision de estudios previos."),
                new TipoItemReadModel("OBJETIVO_ESPECIFICO", "Objetivo Especifico", "Metas concretas y medibles.")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(tiposEsperados);

        // Act
        useCase.ejecutar();

        // Assert
        verify(logger).debug(TipoItemKey.LOG_CONSULTA_COMPLETADA, 2);
    }
}
