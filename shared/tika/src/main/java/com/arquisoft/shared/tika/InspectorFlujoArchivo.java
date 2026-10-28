package com.arquisoft.shared.tika;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

final class InspectorFlujoArchivo extends FilterInputStream {

    private static final String ALGORITMO_HASH = "SHA-256";
    private static final int TAMANO_BUFFER = 8192;

    private final MessageDigest digest;
    private final long limiteBytes;
    private long leidos;

    InspectorFlujoArchivo(InputStream flujo, long limiteBytes) {
        super(flujo);
        this.limiteBytes = limiteBytes;
        try {
            this.digest = MessageDigest.getInstance(ALGORITMO_HASH);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITMO_HASH, e);
        }
    }

    @Override
    public int read() throws IOException {
        if (leidos > limiteBytes) {
            return -1;
        }
        var octeto = super.read();
        if (octeto >= 0) {
            digest.update((byte) octeto);
            leidos++;
        }
        return octeto;
    }

    @Override
    public int read(byte[] destino, int desde, int longitud) throws IOException {
        if (leidos > limiteBytes) {
            return -1;
        }
        var permitido = (int) Math.min(longitud, limiteBytes + 1 - leidos);
        var cantidad = super.read(destino, desde, permitido);
        if (cantidad > 0) {
            digest.update(destino, desde, cantidad);
            leidos += cantidad;
        }
        return cantidad;
    }

    @Override
    public long skip(long cantidad) throws IOException {
        var descartados = 0L;
        var buffer = new byte[TAMANO_BUFFER];
        while (descartados < cantidad) {
            var leido = read(buffer, 0, (int) Math.min(buffer.length, cantidad - descartados));
            if (leido < 0) {
                break;
            }
            descartados += leido;
        }
        return descartados;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void mark(int limite) {
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset no soportado");
    }

    void drenar() throws IOException {
        transferTo(OutputStream.nullOutputStream());
    }

    String hashHexadecimal() {
        return HexFormat.of().formatHex(digest.digest());
    }

    long bytesLeidos() {
        return leidos;
    }
}
