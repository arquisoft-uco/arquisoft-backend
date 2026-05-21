package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilUseCaseImpl;
import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilResponseDTO;
import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
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
class ConsultarFichasPerfilUseCaseImplTest {

    @Mock
    private FichaPerfilRepositoryPort fichaPerfilRepositoryPort;

    @InjectMocks
    private ConsultarFichasPerfilUseCaseImpl consultarFichasPerfilUseCase;

    @Test
    void debeRetornarFichasPaginadas_cuandoExistenFichas() {
        // Arrange
        PaginationRequest request = PaginationRequest.of(0, 10);

        AsesorFicha asesor = AsesorFicha.rebuild(
                UUID.randomUUID(), "DOC-001", "Juan Salazar", "juan.salazar@soyuco.edu.co");
        FichaPerfil ficha = FichaPerfil.rebuild(
                UUID.randomUUID(), "Arquisoft Backend", asesor);

        PaginatedResult<FichaPerfil> resultadoEsperado =
                PaginatedResult.of(List.of(ficha), 0, 10, 1L);

        when(fichaPerfilRepositoryPort.consultarTodas(request))
                .thenReturn(resultadoEsperado);

        // Act
        PaginatedResult<FichaPerfilResponseDTO> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilRepositoryPort, times(1)).consultarTodas(request);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        // Arrange
        PaginationRequest request = PaginationRequest.of(0, 10);

        PaginatedResult<FichaPerfil> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);

        when(fichaPerfilRepositoryPort.consultarTodas(request))
                .thenReturn(resultadoVacio);

        // Act
        PaginatedResult<FichaPerfilResponseDTO> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilRepositoryPort, times(1)).consultarTodas(request);
    }
}
