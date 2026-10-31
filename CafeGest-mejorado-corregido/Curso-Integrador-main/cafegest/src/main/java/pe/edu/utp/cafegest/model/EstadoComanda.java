package pe.edu.utp.cafegest.model;

public enum EstadoComanda {
    PENDIENTE("Pendiente"),
    PREPARANDO("En preparación"),
    LISTO("Listo"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoComanda(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public boolean puedeAvanzarA(EstadoComanda siguiente) {
        return ordinal() + 1 == siguiente.ordinal();
    }
}
