package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProveedoresAutenticacion {

    private final List<ProveedorAutenticacion> proveedores;

    public ProveedoresAutenticacion(List<ProveedorAutenticacion> proveedores) {
        this.proveedores = List.copyOf(proveedores);
    }

    public List<ProveedorAutenticacion> todos() {
        return proveedores;
    }

    public List<ProveedorAutenticacion> habilitados() {
        return proveedores.stream().filter(ProveedorAutenticacion::habilitado).toList();
    }

    public Optional<ProveedorAutenticacion> porId(String id) {
        return habilitados().stream().filter(proveedor -> proveedor.id().equals(id)).findFirst();
    }

    public boolean contiene(String id) {
        return proveedores.stream().anyMatch(proveedor -> proveedor.id().equals(id));
    }
}
