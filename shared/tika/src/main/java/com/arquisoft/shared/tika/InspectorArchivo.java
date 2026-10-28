package com.arquisoft.shared.tika;

import com.arquisoft.shared.tika.model.InspeccionArchivo;

import java.io.IOException;
import java.io.InputStream;

public interface InspectorArchivo {

    InspeccionArchivo inspeccionar(InputStream contenido, long limiteBytes) throws IOException;
}
