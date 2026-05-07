package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
                .identificador("DOC-001")
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
        Page<FichaPerfil> resultado = adapter.consultarTodas(PageRequest.of(0, 10));

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);

        // Verifica que el adapter usa rebuild() — los datos de dominio son correctos
        FichaPerfil fichaReconstruida = resultado.getContent().get(0);
        assertThat(fichaReconstruida.getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(fichaReconstruida.getAsesorFicha().getNombre()).isEqualTo("Juan Salazar");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichasEnBD() {
        // Arrange — BD vacía (sin datos previos)

        // Act
        Page<FichaPerfil> resultado = adapter.consultarTodas(PageRequest.of(0, 10));

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
    }
}
