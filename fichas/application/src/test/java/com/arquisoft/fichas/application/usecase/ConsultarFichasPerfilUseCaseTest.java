package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilUseCaseTest {

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Test
    void debeRetornarFichasPaginadas_cuandoExistenFichas() {
        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();

        FichaPerfilReadModel ficha = FichaPerfilReadModel.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Arquisoft Backend")
                .build();

        PaginatedResult<FichaPerfilReadModel> resultadoEsperado =
                PaginatedResult.of(List.of(ficha), 0, 10, 1L);

        when(fichaPerfilQueryOutputPort.consultarTodas(criteria)).thenReturn(resultadoEsperado);

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(criteria);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilQueryOutputPort, times(1)).consultarTodas(criteria);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();

        PaginatedResult<FichaPerfilReadModel> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);

        when(fichaPerfilQueryOutputPort.consultarTodas(criteria)).thenReturn(resultadoVacio);

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(criteria);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilQueryOutputPort, times(1)).consultarTodas(criteria);
    }
}
