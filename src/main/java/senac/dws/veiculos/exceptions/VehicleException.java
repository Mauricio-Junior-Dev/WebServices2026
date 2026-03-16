package senac.dws.veiculos.exceptions;

public class VehicleException extends Exception {
    public VehicleException(long id) {
        super("Could not find vehicle with id " + id);
    }
}
