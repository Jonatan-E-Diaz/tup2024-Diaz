package ar.edu.utn.frbb.tup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaTipoNotSupportException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;


@Component
public class CuentaService {
    CuentaDao cuentaDao = new CuentaDao();

    @Autowired
    ClienteService clienteService;

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente
    
    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, CuentaTipoNotSupportException, TipoCuentaAlreadyExistsException {
        
        //Chequear que la cuenta no exista
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        //Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
        if (!tipoDeCuentaSoportada(cuenta)) {
            throw new CuentaTipoNotSupportException("La cuenta " + cuenta.getNumeroCuenta() + " no es soportada por el banco.");
        }
        
        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }
    
    public boolean tipoDeCuentaSoportada(Cuenta cuenta) {
        TipoCuenta tipoCuenta = cuenta.getTipoCuenta();
        TipoMoneda moneda = cuenta.getMoneda();
        return (tipoCuenta == TipoCuenta.CUENTA_CORRIENTE && moneda == TipoMoneda.PESOS) ||
               (tipoCuenta == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.PESOS) ||
               (tipoCuenta == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.DOLARES);
    }
}
