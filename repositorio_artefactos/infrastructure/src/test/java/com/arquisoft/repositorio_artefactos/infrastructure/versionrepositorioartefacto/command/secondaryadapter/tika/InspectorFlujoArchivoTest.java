package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.tika;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InspectorFlujoArchivoTest {

    private static final long SIN_LIMITE = Long.MAX_VALUE - 1;

    private static byte[] aleatorio(int tamano) {
        var bytes = new byte[tamano];
        new Random(42).nextBytes(bytes);
        return bytes;
    }

    private static String sha256(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    @Test
    void debeCoincidirElHashConMessageDigestDirecto() throws Exception {
        // Arrange
        var bytes = aleatorio(50_000);
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(bytes), SIN_LIMITE);

        // Act
        inspector.drenar();

        // Assert
        assertThat(inspector.hashHexadecimal()).isEqualTo(sha256(bytes));
        assertThat(inspector.bytesLeidos()).isEqualTo(bytes.length);
    }

    @Test
    void debeCompletarElHash_cuandoElFlujoSeLeyoAMedias() throws Exception {
        // Arrange
        var bytes = aleatorio(30_000);
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(bytes), SIN_LIMITE);
        var primeros = new byte[100];
        inspector.read(primeros, 0, primeros.length);
        inspector.read();

        // Act
        inspector.drenar();

        // Assert
        assertThat(inspector.hashHexadecimal()).isEqualTo(sha256(bytes));
    }

    @Test
    void debeIncluirEnElHash_losBytesDescartadosConSkip() throws Exception {
        // Arrange
        var bytes = aleatorio(20_000);
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(bytes), SIN_LIMITE);

        // Act
        var saltados = inspector.skip(12_345);
        inspector.drenar();

        // Assert
        assertThat(saltados).isEqualTo(12_345);
        assertThat(inspector.hashHexadecimal()).isEqualTo(sha256(bytes));
    }

    @Test
    void debeCortarEnElLimiteMasUno() throws Exception {
        // Arrange
        var bytes = aleatorio(1_000);
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(bytes), 100);

        // Act
        inspector.drenar();

        // Assert
        assertThat(inspector.bytesLeidos()).isEqualTo(101);
        assertThat(inspector.read()).isEqualTo(-1);
        assertThat(inspector.read(new byte[10], 0, 10)).isEqualTo(-1);
    }

    @Test
    void debeNoSoportarMarkNiReset() {
        // Arrange
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(new byte[1]), SIN_LIMITE);

        // Act
        inspector.mark(10);

        // Assert
        assertThat(inspector.markSupported()).isFalse();
        assertThatThrownBy(inspector::reset).isInstanceOf(IOException.class);
    }

    @Test
    void debeDetenerSkip_cuandoElFlujoTermina() throws Exception {
        // Arrange
        var inspector = new InspectorFlujoArchivo(new ByteArrayInputStream(new byte[10]), SIN_LIMITE);

        // Act & Assert
        assertThat(inspector.skip(100)).isEqualTo(10);
    }
}
