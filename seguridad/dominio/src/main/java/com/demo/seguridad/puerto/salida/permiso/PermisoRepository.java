package com.demo.seguridad.puerto.salida.permiso;

import com.demo.seguridad.modelo.Permiso;

import java.util.List;

public interface PermisoRepository {
    Integer crearPermiso(Permiso permiso);
    List<Permiso> consultarTodosLosPermisos();
    void editarPermiso(Permiso permiso);
}
