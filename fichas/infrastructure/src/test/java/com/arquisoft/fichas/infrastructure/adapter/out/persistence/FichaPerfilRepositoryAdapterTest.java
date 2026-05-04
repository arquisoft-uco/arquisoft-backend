package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.shared.domain.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FichaPerfilRepositoryAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilJpaRepository fichaPerfilJpaRepository;

    private FichaPerfilRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilRepositoryAdapter(fichaPerfilJpaRepository);
    }

    @Test
    void debeRetornarFichasConRebuild_cuandoExistenEnBD() {
        // Arrange — AsesorFichaJpaEntity sin @GeneratedValue: UUID manual obligatorio
        AsesorFichaJpaEntity asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .nombre("Juan Salazar")
                .email("juan.salazar@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        FichaPerfilJpaEntity ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Arquisoft Backend")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        entityManager.flush();

        // Act
        Page<FichaPerfil> resultado = adapter.consultarPaginadas(0, 10);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.totalElements()).isEqualTo(1L);

        // Verifica que el adapter usa rebuild() — la entidad NO debe tener eventos pendientes
        FichaPerfil fichaReconstruida = resultado.content().get(0);
        assertThat(fichaReconstruida.getUnPublishedEvents()).isEmpty();
        assertThat(fichaReconstruida.getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(fichaReconstruida.getAsesorFicha().getNombre()).isEqualTo("Juan Salazar");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichasEnBD() {
        // Arrange — BD vacía (sin datos previos)

        // Act
        Page<FichaPerfil> resultado = adapter.consultarPaginadas(0, 10);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.content()).isEmpty();
        assertThat(resultado.totalElements()).isZero();
        assertThat(resultado.page()).isEqualTo(0);
        assertThat(resultado.size()).isEqualTo(10);
    }
}
