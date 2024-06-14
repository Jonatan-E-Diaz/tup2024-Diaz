package ar.edu.utn.frbb.tup.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;

import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaTipoNotSupportException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class CuentaServiceTest {
  
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

	@Test
	public void testCuentaExistente() {

		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta(123456789);
		cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
		cuenta.setMoneda(TipoMoneda.PESOS);

		when(cuentaDao.find(123456789)).thenReturn(new Cuenta());

		assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 264564370));

	}

	@Test
	public void testCuentaNoSoportada() {

		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta(123456789);
		cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
		cuenta.setMoneda(TipoMoneda.DOLARES);

		when(cuentaDao.find(123456789)).thenReturn(null);
		assertThrows(CuentaTipoNotSupportException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 264564370));
	}

    @Test
	public void testTipoCuentaExistente()
			throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, CuentaTipoNotSupportException {
		Cuenta cuenta = new Cuenta().setMoneda(TipoMoneda.PESOS).setBalance(10000)
				.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
		cuenta.setNumeroCuenta(123456789);

		doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta, 29857643);
		assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 29857643));

	}

	@Test
	 public void testCuentaCreadaExitosamente() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, ClienteAlreadyExistsException, CuentaTipoNotSupportException {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(10000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setNumeroCuenta(123456789);

        when(cuentaDao.find(123456789)).thenReturn(null);

        cuentaService.darDeAltaCuenta(cuenta, 29857643);
        verify(cuentaDao, times(1)).save(cuenta);

    }

}


