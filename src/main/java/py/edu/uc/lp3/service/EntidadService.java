package py.edu.uc.lp3.service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.MobHostil;
import py.edu.uc.lp3.domain.OtraEntidad;
import py.edu.uc.lp3.domain.Residente;
import py.edu.uc.lp3.domain.SerVivo;
import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.EntidadRequest.Atributos;
import py.edu.uc.lp3.dto.EntidadResponse;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;

@Service
@Transactional(readOnly = true)
public class EntidadService {
    private final EntidadRepository repository;

    public EntidadService(EntidadRepository repository) {
        this.repository = repository;
    }

    public List<EntidadResponse> listar() {
        return repository.findAll().stream().map(this::respuesta).toList();
    }

    public EntidadResponse obtener(UUID id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public EntidadResponse crear(EntidadRequest datos) {
        validar(datos);
        Entidad entidad = construir(datos);
        entidad.actualizarDatos(datos.nombre().strip(), datos.salud(), posicion(datos));
        return respuesta(repository.save(entidad));
    }

    @Transactional
    public EntidadResponse actualizar(UUID id, EntidadRequest datos) {
        Entidad entidad = buscar(id);
        validar(datos);
        if (entidad.getTipo() != datos.tipo()) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de una entidad existente");
        }
        entidad.actualizarDatos(datos.nombre().strip(), datos.salud(), posicion(datos));
        aplicarAtributos(entidad, atributos(datos));
        return respuesta(repository.save(entidad));
    }

    @Transactional
    public void eliminar(UUID id) {
        buscar(id);
        repository.desvincularObjetivo(id);
        repository.deleteById(id);
    }

    private Entidad buscar(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntidadNoEncontradaException(id));
    }

    private void validar(EntidadRequest datos) {
        if (datos == null || datos.nombre() == null || datos.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (datos.nombre().strip().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres");
        }
        if (datos.tipo() == null) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        if (datos.salud() == null || !Double.isFinite(datos.salud()) || datos.salud() < 0) {
            throw new IllegalArgumentException("La salud debe ser finita y no negativa");
        }
        var ubicacion = datos.ubicacion();
        if (ubicacion == null || ubicacion.x() == null || ubicacion.y() == null || ubicacion.z() == null) {
            throw new IllegalArgumentException("La ubicación debe incluir x, y y z");
        }
        // Reutiliza la regla del dominio que exige coordenadas finitas.
        new Vector3D(ubicacion.x(), ubicacion.y(), ubicacion.z());
        Atributos a = atributos(datos);
        exigirTipo(a.velocidad(), datos.tipo(), TipoEntidad.ZOMBIE, "velocidad");
        exigirTipo(a.fuseTime(), datos.tipo(), TipoEntidad.CREEPER, "fuseTime");
        exigirTipo(a.tieneArco(), datos.tipo(), TipoEntidad.ESQUELETO, "tieneArco");
        exigirTipo(a.puedeTomarBloques(), datos.tipo(), TipoEntidad.ENDERMAN, "puedeTomarBloques");
        exigirTipo(a.montable(), datos.tipo(), TipoEntidad.CERDO, "montable");
        exigirTipo(a.edad(), datos.tipo(), TipoEntidad.CERDO, "edad");
        exigirTipo(a.profesion(), datos.tipo(), TipoEntidad.ALDEANO, "profesion");
        exigirTipo(a.nivelComercio(), datos.tipo(), TipoEntidad.ALDEANO, "nivelComercio");
        exigirTipo(a.capacidadInventario(), datos.tipo(), TipoEntidad.JUGADOR, "capacidadInventario");
        exigirTipo(a.modoCreativo(), datos.tipo(), TipoEntidad.JUGADOR, "modoCreativo");

        validarAtributosSegunTipo(a, datos.tipo());
    }

    private void exigirTipo(Object valor, TipoEntidad actual, TipoEntidad esperado, String campo) {
        if (valor != null && actual != esperado) {
            throw new IllegalArgumentException("El atributo " + campo + " solo corresponde a " + esperado);
        }
    }

    private void validarAtributosSegunTipo(Atributos a, TipoEntidad tipo) {
        if (a == null) {
            return;
        }
        if (tipo == TipoEntidad.ZOMBIE && a.velocidad() != null && (!Double.isFinite(a.velocidad()) || a.velocidad() < 0)) {
            throw new IllegalArgumentException("La velocidad debe ser finita y no negativa");
        }
        if (tipo == TipoEntidad.CREEPER && a.fuseTime() != null && a.fuseTime() <= 0) {
            throw new IllegalArgumentException("El tiempo de explosión debe ser mayor que cero");
        }
        if (tipo == TipoEntidad.CERDO && a.edad() != null && a.edad() < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        if (tipo == TipoEntidad.ALDEANO) {
            if (a.profesion() != null && (a.profesion().isBlank() || a.profesion().length() > 255)) {
                throw new IllegalArgumentException("La profesión debe tener entre 1 y 255 caracteres");
            }
            if (a.nivelComercio() != null && (a.nivelComercio() < 1 || a.nivelComercio() > 5)) {
                throw new IllegalArgumentException("El nivel de comercio debe estar entre 1 y 5");
            }
        }
        if (tipo == TipoEntidad.JUGADOR && a.capacidadInventario() != null && a.capacidadInventario() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
    }

    private Entidad construir(EntidadRequest datos) {
        String nombre = datos.nombre().strip();
        Vector3D posicion = posicion(datos);
        Atributos a = atributos(datos);
        return switch (datos.tipo()) {
            case ZOMBIE -> new Zombie(nombre, posicion, valor(a.velocidad(), 1.5));
            case CREEPER -> new Creeper(nombre, posicion, valor(a.fuseTime(), 30));
            case ESQUELETO -> new Esqueleto(nombre, posicion, valor(a.tieneArco(), true));
            case ENDERMAN -> new Enderman(nombre, posicion, valor(a.puedeTomarBloques(), true));
            case CERDO -> new Cerdo(nombre, posicion, valor(a.montable(), false), valor(a.edad(), 0));
            case ALDEANO -> new Aldeano(nombre, posicion, valor(a.profesion(), "Granjero"),
                    valor(a.nivelComercio(), 1));
            case JUGADOR -> new Jugador(nombre, posicion, valor(a.capacidadInventario(), 36),
                    valor(a.modoCreativo(), false));
            case OTRO -> new OtraEntidad(nombre, posicion);
        };
    }

    private void aplicarAtributos(Entidad entidad, Atributos a) {
        switch (entidad) {
            case Zombie zombie -> zombie.actualizarVelocidad(valor(a.velocidad(), zombie.getVelocidad()));
            case Creeper creeper -> {
                if (a.fuseTime() != null) creeper.actualizarTiempoExplosion(a.fuseTime());
            }
            case Esqueleto esqueleto -> esqueleto.actualizarArco(valor(a.tieneArco(), esqueleto.isTieneArco()));
            case Enderman enderman -> enderman.actualizarPuedeTomarBloques(
                    valor(a.puedeTomarBloques(), enderman.isPuedeTomarBloques()));
            case Cerdo cerdo -> cerdo.actualizarCaracteristicas(valor(a.montable(), cerdo.isMontable()),
                    valor(a.edad(), cerdo.getEdad()));
            case Aldeano aldeano -> aldeano.actualizarComercio(valor(a.profesion(), aldeano.getProfesion()),
                    valor(a.nivelComercio(), aldeano.getNivelComercio()));
            case Jugador jugador -> {
                if (a.capacidadInventario() != null
                        && a.capacidadInventario() != jugador.abrirInventario().getCapacidad()) {
                    throw new IllegalArgumentException("No se puede cambiar la capacidad del inventario existente");
                }
                jugador.actualizarModoCreativo(valor(a.modoCreativo(), jugador.isModoCreativo()));
            }
            default -> { }
        }
    }

    private Vector3D posicion(EntidadRequest datos) {
        return new Vector3D(datos.ubicacion().x(), datos.ubicacion().y(), datos.ubicacion().z());
    }

    private Atributos atributos(EntidadRequest datos) {
        return datos.atributos() == null ? new Atributos() : datos.atributos();
    }

    private <T> T valor(T valor, T predeterminado) {
        return valor == null ? predeterminado : valor;
    }

    private EntidadResponse respuesta(Entidad entidad) {
        Map<String, Object> a = new LinkedHashMap<>();
        if (entidad instanceof SerVivo vivo) {
            a.put("saludMaxima", vivo.getSaludMaxima());
            a.put("regeneracion", vivo.getRegeneracion());
            a.put("envenenado", vivo.isEnvenenado());
        }
        if (entidad instanceof MobHostil mob) {
            a.put("fuerzaAtaque", mob.getFuerzaAtaque());
            a.put("alcanceVision", mob.getAlcanceVision());
        }
        if (entidad instanceof Animal animal) {
            a.put("domesticable", animal.esDomesticable());
            a.put("edad", animal.getEdad());
            a.put("adulto", animal.isAdulto());
        }
        if (entidad instanceof Residente residente) a.put("puedeComerciar", residente.isPuedeComerciar());
        switch (entidad) {
            case Zombie zombie -> {
                a.put("velocidad", zombie.getVelocidad());
                a.put("quemandose", zombie.isQuemandose());
            }
            case Creeper creeper -> {
                a.put("fuseTime", creeper.getFuseTime());
                a.put("encendido", creeper.isEncendido());
            }
            case Esqueleto esqueleto -> a.put("tieneArco", esqueleto.isTieneArco());
            case Enderman enderman -> a.put("puedeTomarBloques", enderman.isPuedeTomarBloques());
            case Cerdo cerdo -> a.put("montable", cerdo.isMontable());
            case Aldeano aldeano -> {
                a.put("profesion", aldeano.getProfesion());
                a.put("nivelComercio", aldeano.getNivelComercio());
            }
            case Jugador jugador -> {
                a.put("modoCreativo", jugador.isModoCreativo());
                a.put("nivelExperiencia", jugador.getNivelExperiencia());
                a.put("capacidadInventario", jugador.abrirInventario().getCapacidad());
                a.put("items", jugador.abrirInventario().getItems());
            }
            default -> { }
        }
        return new EntidadResponse(entidad.getId(), entidad.getNombre(), entidad.getTipo(), entidad.getSalud(),
                entidad.obtenerPosicion(), a);
    }
}
