package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.port.out.FichaPerfilOutputPort;
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
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @InjectMocks
    private ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Test
    void debeRetornarFichasPaginadas_cuandoExistenFichas() {
        // Arrange
        PaginationRequest request = PaginationRequest.of(0, 10);

        AsesorFicha asesor = AsesorFicha.rebuild(
                UUID.randomUUID(), "DOC-001", "Juan Salazar", "juan.salazar@soyuco.edu.co");
        FichaPerfilAggregate ficha = FichaPerfilAggregate.rebuild(
                UUID.randomUUID(), "Arquisoft Backend", asesor);

        PaginatedResult<FichaPerfilAggregate> resultadoEsperado =
                PaginatedResult.of(List.of(ficha), 0, 10, 1L);

        when(fichaPerfilOutputPort.consultarTodas(request))
                .thenReturn(resultadoEsperado);

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilOutputPort, times(1)).consultarTodas(request);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        // Arrange
        PaginationRequest request = PaginationRequest.of(0, 10);

        PaginatedResult<FichaPerfilAggregate> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);

        when(fichaPerfilOutputPort.consultarTodas(request))
                .thenReturn(resultadoVacio);

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilOutputPort, times(1)).consultarTodas(request);
    }
}
