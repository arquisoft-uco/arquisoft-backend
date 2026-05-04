package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import com.arquisoft.shared.domain.Page;
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
        int page = 0;
        int size = 10;

        AsesorFicha asesor = AsesorFicha.of(
                UUID.randomUUID(), "Juan Salazar", "juan.salazar@soyuco.edu.co");
        FichaPerfil ficha = FichaPerfil.rebuild(
                UUID.randomUUID(), "Arquisoft Backend", asesor);

        Page<FichaPerfil> paginaEsperada = Page.of(List.of(ficha), page, size, 1L);

        when(fichaPerfilRepositoryPort.consultarPaginadas(page, size))
                .thenReturn(paginaEsperada);

        // Act
        Page<FichaPerfil> resultado = consultarFichasPerfilUseCase.ejecutar(page, size);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.totalElements()).isEqualTo(1L);
        assertThat(resultado.page()).isEqualTo(page);
        assertThat(resultado.size()).isEqualTo(size);
        verify(fichaPerfilRepositoryPort, times(1)).consultarPaginadas(page, size);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        // Arrange
        int page = 0;
        int size = 10;

        Page<FichaPerfil> paginaVacia = Page.empty(page, size);

        when(fichaPerfilRepositoryPort.consultarPaginadas(page, size))
                .thenReturn(paginaVacia);

        // Act
        Page<FichaPerfil> resultado = consultarFichasPerfilUseCase.ejecutar(page, size);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.content()).isEmpty();
        assertThat(resultado.totalElements()).isZero();
        assertThat(resultado.page()).isEqualTo(page);
        assertThat(resultado.size()).isEqualTo(size);
        verify(fichaPerfilRepositoryPort, times(1)).consultarPaginadas(page, size);
    }
}
