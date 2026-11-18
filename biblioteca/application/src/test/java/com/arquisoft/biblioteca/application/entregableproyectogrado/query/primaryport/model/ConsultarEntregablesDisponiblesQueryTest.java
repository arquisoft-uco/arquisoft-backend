package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEntregablesDisponiblesQueryTest {

    @Test
    void debeGuardarElTituloRecortado_cuandoTraeEspacios() {
        // Act
        var query = ConsultarEntregablesDisponiblesQuery.crear(2, 15, "  gestión  ");

        // Assert
        assertThat(query.titulo()).isEqualTo("gestión");
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(15);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeGuardarTituloVacio_cuandoNoSeEnvia() {
        // Act
        var query = ConsultarEntregablesDisponiblesQuery.crear(0, 10, null);

        // Assert
        assertThat(query.titulo()).isEmpty();
    }

    @Test
    void debeAceptarLosLimites_cuandoLosValoresEstanEnElBorde() {
        // Arrange
        var tituloDe100 = "a".repeat(100);

        // Act
        var primero = ConsultarEntregablesDisponiblesQuery.crear(0, 1, "  " + tituloDe100 + "  ");
        var ultimo = ConsultarEntregablesDisponiblesQuery.crear(0, 100, tituloDe100);

        // Assert
        assertThat(primero.titulo()).isEqualTo(tituloDe100);
        assertThat(primero.criterio().tamanio()).isEqualTo(1);
        assertThat(ultimo.criterio().tamanio()).isEqualTo(100);
    }

    @Test
    void debeLanzar400_cuandoTamanioEstaFueraDeRango() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEntregablesDisponiblesQuery.crear(0, 0, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactly(BibliotecaCodes.EntregableProyectoGrado.TAMANIO_INVALIDO));
        assertThatThrownBy(() -> ConsultarEntregablesDisponiblesQuery.crear(0, 101, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactly(BibliotecaCodes.EntregableProyectoGrado.TAMANIO_INVALIDO));
    }

    @Test
    void debeAcumularLosTresErrores_cuandoPaginaTamanioYTituloSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEntregablesDisponiblesQuery.crear(-1, 0, "a".repeat(101)))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.EntregableProyectoGrado.PAGINA_INVALIDA,
                                BibliotecaCodes.EntregableProyectoGrado.TAMANIO_INVALIDO,
                                BibliotecaCodes.EntregableProyectoGrado.TITULO_LONGITUD_EXCEDIDA));
    }
}
