package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilAsesoradasUseCaseImplTest {

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarFichasPerfilAsesoradasUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoYRetornarResultado_cuandoSeEjecuta() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();
        var ficha = new FichaPerfilReadModel(UUID.randomUUID(), "Arquisoft Backend", null);
        var resultadoEsperado = PaginatedResult.of(List.of(ficha), 0, 10, 1L);

        when(fichaPerfilQueryOutputPort.consultarTodas(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(fichaPerfilQueryOutputPort).consultarTodas(criteria);
    }

    @Test
    void debeLoguearEntradaYCierre_conFichaPerfilKey() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();
        var resultado = PaginatedResult.of(List.<FichaPerfilReadModel>of(), 0, 10, 0L);
        when(fichaPerfilQueryOutputPort.consultarTodas(criteria)).thenReturn(resultado);

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(FichaPerfilKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(FichaPerfilKey.LOG_CONSULTA_COMPLETADA), eq(0L), eq(0), eq(10));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraFichas() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<FichaPerfilReadModel>of(), 0, 10, 0L);
        when(fichaPerfilQueryOutputPort.consultarTodas(any(FichaPerfilCriteria.class)))
                .thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
