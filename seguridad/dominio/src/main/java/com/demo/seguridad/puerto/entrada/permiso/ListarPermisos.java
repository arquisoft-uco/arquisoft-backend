package com.demo.seguridad.puerto.entrada.permiso;

import com.demo.seguridad.modelo.Aplicacion;
import com.demo.seguridad.modelo.Permiso;

import java.util.List;

public interface ListarPermisos {
    List<Permiso> listarPermisos(Aplicacion aplicacion);
}
