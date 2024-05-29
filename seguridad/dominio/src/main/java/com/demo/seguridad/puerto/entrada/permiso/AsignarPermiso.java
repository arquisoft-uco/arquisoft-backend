package com.demo.seguridad.puerto.entrada.permiso;

import com.demo.seguridad.modelo.Permiso;
import com.demo.seguridad.modelo.Rol;

public interface AsignarPermiso {
    void asignarPermiso(Permiso permiso, Rol rol);
}
