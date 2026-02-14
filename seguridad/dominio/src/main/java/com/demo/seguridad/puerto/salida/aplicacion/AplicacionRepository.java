package com.demo.seguridad.puerto.salida.aplicacion;

import com.demo.seguridad.modelo.Aplicacion;

import java.util.List;

public interface AplicacionRepository {
    Integer crearAplicacion(Aplicacion aplicacion);
    List<Aplicacion> consultarTodasLasAplicaciones();
    void editarAplicacion(Aplicacion aplicacion);
}
