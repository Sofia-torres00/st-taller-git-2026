package py.edu.uc.lp3.minecraft;

public abstract class SerVivo extends Entidad {
    private final double saludMaxima;
    private boolean envenenado;
    private final double regeneracion;

    protected SerVivo(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            double saludMaxima, double regeneracion) {
        super(nombre, tipo, validarSaludInicial(salud, saludMaxima), ubicacion);
        this.saludMaxima = saludMaxima;
        this.regeneracion = validarCantidad(regeneracion, "La regeneración");
    }

    public final void envenenar() {
        envenenado = true;
    }

    public final void curarVeneno() {
        envenenado = false;
    }

    protected final void actualizarEstadoVital() {
        if (envenenado) {
            tomarDanio(1);
        } else {
            curar(regeneracion);
        }
    }

    @Override
    protected final double limiteSalud() {
        return saludMaxima;
    }

    public final double getSaludMaxima() {
        return saludMaxima;
    }

    public final boolean isEnvenenado() {
        return envenenado;
    }

    public final double getRegeneracion() {
        return regeneracion;
    }

    private static double validarSaludInicial(double salud, double saludMaxima) {
        validarCantidad(saludMaxima, "La salud máxima");
        if (saludMaxima == 0) {
            throw new IllegalArgumentException("La salud máxima debe ser mayor que cero");
        }
        validarCantidad(salud, "La salud");
        if (salud > saludMaxima) {
            throw new IllegalArgumentException("La salud no puede superar la salud máxima");
        }
        return salud;
    }
}
