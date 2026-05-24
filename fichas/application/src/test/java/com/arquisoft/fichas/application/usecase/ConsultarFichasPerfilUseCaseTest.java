package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
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

    @InjectMocks
    private ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Test
    void debeRetornarFichasPaginadas_cuandoExistenFichas() {
        PaginationRequest request = PaginationRequest.of(0, 10);

        FichaPerfilReadModel ficha = FichaPerfilReadModel.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Arquisoft Backend")
                .build();

        PaginatedResult<FichaPerfilReadModel> resultadoEsperado =
                PaginatedResult.of(List.of(ficha), 0, 10, 1L);

        when(fichaPerfilQueryOutputPort.consultarTodas(request)).thenReturn(resultadoEsperado);

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilQueryOutputPort, times(1)).consultarTodas(request);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        PaginationRequest request = PaginationRequest.of(0, 10);

        PaginatedResult<FichaPerfilReadModel> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);

        when(fichaPerfilQueryOutputPort.consultarTodas(request)).thenReturn(resultadoVacio);

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilQueryOutputPort, times(1)).consultarTodas(request);
    }
}
