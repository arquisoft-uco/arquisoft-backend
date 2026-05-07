package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        Pageable pageable = PageRequest.of(0, 10);

        AsesorFicha asesor = AsesorFicha.of(
                UUID.randomUUID(), "DOC-001", "Juan Salazar", "juan.salazar@soyuco.edu.co");
        FichaPerfil ficha = FichaPerfil.rebuild(
                UUID.randomUUID(), "Arquisoft Backend", asesor);

        Page<FichaPerfil> paginaEsperada = new PageImpl<>(List.of(ficha), pageable, 1L);

        when(fichaPerfilRepositoryPort.consultarTodas(pageable))
                .thenReturn(paginaEsperada);

        // Act
        Page<FichaPerfil> resultado = consultarFichasPerfilUseCase.ejecutar(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilRepositoryPort, times(1)).consultarTodas(pageable);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Page<FichaPerfil> paginaVacia = Page.empty(pageable);

        when(fichaPerfilRepositoryPort.consultarTodas(pageable))
                .thenReturn(paginaVacia);

        // Act
        Page<FichaPerfil> resultado = consultarFichasPerfilUseCase.ejecutar(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        verify(fichaPerfilRepositoryPort, times(1)).consultarTodas(pageable);
    }
}
