package com.arquisoft.biblioteca.domain.entregableproyectogrado;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoDomainTest {

    @Test
    void debeReconstruirConLosCuatroAtributosYSerVacioElCentinela_cuandoSeUsaCadaFabrica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, "entregables", "a/b.zip");

        // Assert
        assertThat(entregable.esVacio()).isFalse();
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(entregable.getBucket()).isEqualTo("entregables");
        assertThat(entregable.getClaveObjeto()).isEqualTo("a/b.zip");
        assertThat(EntregableProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EntregableProyectoGradoDomain.VACIO.getBucket()).isEqualTo(UtilTexto.VACIO);
    }
}
