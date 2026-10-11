package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.criteria.CategoriaItemCuantitativoJuradoCriteria;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.command.secondaryadapter.entity.CategoriaItemCuantitativoJuradoJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CategoriaItemCuantitativoJuradoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoriaItemCuantitativoJuradoQueryRepository repository;

    private CategoriaItemCuantitativoJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CategoriaItemCuantitativoJuradoQueryOutputAdapter(repository);
    }

    private UUID sembrar(String nombre, String descripcion) {
        var id = UUID.randomUUID();
        entityManager.persist(CategoriaItemCuantitativoJuradoJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
        entityManager.flush();
        entityManager.clear();
        return id;
    }

    @Test
    void debeRetornarTodasOrdenadasPorNombre_cuandoNoHayFiltro() {
        // Arrange
        sembrar("Rigor", "Evalúa el rigor");
        sembrar("Coherencia", "Evalúa la coherencia");
        sembrar("Puntualidad", "Evalúa la puntualidad");

        // Act
        var resultado = adapter.consultar(new CategoriaItemCuantitativoJuradoCriteria(null));

        // Assert
        assertThat(resultado)
                .extracting(categoria -> categoria.nombre())
                .containsExactly("Coherencia", "Puntualidad", "Rigor");
    }

    @Test
    void debeFiltrarPorCoincidenciaParcialSinDistinguirMayusculas_cuandoHayNombre() {
        // Arrange
        sembrar("Rigor metodológico", "Evalúa el rigor");
        sembrar("Coherencia", "Evalúa la coherencia");
        sembrar("Puntualidad", "Evalúa la puntualidad");

        // Act
        var resultado = adapter.consultar(new CategoriaItemCuantitativoJuradoCriteria("RIGOR"));

        // Assert
        assertThat(resultado)
                .extracting(categoria -> categoria.nombre())
                .containsExactly("Rigor metodológico");
    }

    @Test
    void debeOrdenarElResultadoFiltradoPorNombre_cuandoVariasCoinciden() {
        // Arrange
        sembrar("Calidad de redacción", "Evalúa la redacción");
        sembrar("Calidad técnica", "Evalúa lo técnico");
        sembrar("Alcance", "Evalúa el alcance");

        // Act
        var resultado = adapter.consultar(new CategoriaItemCuantitativoJuradoCriteria("calidad"));

        // Assert
        assertThat(resultado)
                .extracting(categoria -> categoria.nombre())
                .containsExactly("Calidad de redacción", "Calidad técnica");
    }

    @Test
    void debeMapearIdNombreYDescripcion_deLaColumnaDelSubselect() {
        // Arrange
        var id = sembrar("Coherencia", "Evalúa la coherencia argumentativa");

        // Act
        var resultado = adapter.consultar(new CategoriaItemCuantitativoJuradoCriteria(null));

        // Assert
        var readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.nombre()).isEqualTo("Coherencia");
        assertThat(readModel.descripcion()).isEqualTo("Evalúa la coherencia argumentativa");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayCoincidencias() {
        // Arrange
        sembrar("Coherencia", "Evalúa la coherencia");

        // Act
        var resultado = adapter.consultar(new CategoriaItemCuantitativoJuradoCriteria("inexistente"));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
