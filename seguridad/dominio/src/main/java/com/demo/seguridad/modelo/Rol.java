package com.demo.seguridad.modelo;

import java.util.List;

public class Rol {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Aplicacion aplicacion;
    private List<Permiso> permisos;
}
