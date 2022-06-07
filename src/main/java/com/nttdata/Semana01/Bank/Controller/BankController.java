package com.nttdata.Semana01.Bank.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
 
import com.nttdata.Semana01.Bank.DTO.ClientProducts;
import com.nttdata.Semana01.Bank.DTO.TypeBankAccounts;
import com.nttdata.Semana01.Bank.DTO.TypeCredits;
import com.nttdata.Semana01.Bank.Entity.Bank;
import com.nttdata.Semana01.Bank.Service.BankService;
import com.nttdata.Semana01.Bank.response.BankResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bank")
public class BankController {

	@Autowired
	BankService bankSerivce;

	private String codigoValidator; 

	List<TypeBankAccounts> listTypeBankAccountsFinal = new ArrayList<>();
	
	List<TypeCredits> listCreditsFinal = new ArrayList<>();
	
	// Registrar Banco

	@PostMapping
	public Mono<Bank> createBank(@RequestBody Bank bank) throws InterruptedException {

		boolean validationvalue = this.validationRegisterBankRequest(bank);

		if (validationvalue) {

			List<TypeBankAccounts> typeBankAccounts = new ArrayList<>();
			List<TypeCredits> typeCredits = new ArrayList<>();

			/*
			 * 
			 * 1. Omicion de llamado de WebClient en controlador
			 * 
			 * Flux<TypeBankAccounts> typeBankAccountServiceClientResponse =
			 * typeBankAccountServiceClient.get()
			 * //.uri("/customer/".concat(bankAccounts.getCustomer().getCodeCustomer()))
			 * //.uri("/typeBankAccounts/typeBankAccountbyId/1") .uri("/typeBankAccounts")
			 * .accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(TypeBankAccounts.
			 * class) .log() .doOnError(ex -> { throw new
			 * RuntimeException("the exception message is - " + ex.getMessage()); });
			 * 
			 * 
			 */

			
			/* 
			    Descomentar para consumir Servicio de TypeBankAccountService
			  
			  	Sin mock
				
				Flux<TypeBankAccounts> typeBankAccountServiceClientResponse = this.bankSerivce
						.comunicationWebClientObtenerTypeBankAccounts();
	
				typeBankAccountServiceClientResponse.collectList().subscribe(typeBankAccounts::addAll);
			
			*/
			
			/* 
			    Descomentar para consumir Servicio de TypeCredits
			  
			  	Sin mock 
				
				Flux<TypeCredits> typeCreditsServiceClientResponse = this.bankSerivce
						.comunicationWebClientObtenerTypeCredits();
	
				typeCreditsServiceClientResponse.collectList().subscribe(typeCredits::addAll);
		
				log.info("Validar typecredits --> "+typeCredits);
			
			*/
			
			// Con Mock
			
			typeBankAccounts = this.comunicationWebClientObtenerTypeBankAccountsMock();
			typeCredits = this.comunicationWebClientObtenerTypeCreditsMock();
			
			/*
			 * Se agrego una busqueda por Codigo; por la razon que el id que se generar para
			 * el registro de Banco es un autogeneracion Randon que nos brindan MongoDB; y
			 * transladando a casos reales es de preferencia no relacionar las consultas por
			 * ID, si no por un atributo Codigo para identificar con mas precision el datos
			 * que se desea consultar
			 */

			Flux<Bank> list = this.bankSerivce.getAllBankByCode(bank.getCode());

			List<Bank> list1 = new ArrayList<>();

			list.collectList().subscribe(list1::addAll);

			try {

				// Se agrego un Sleep para detener el proceso para obtener el valor deseado del
				// Atributo list1

				long temporizador = (6 * 1000);
 				Thread.sleep(temporizador);

				if (list1.isEmpty()) {
					codigoValidator = "";
				} else {
					codigoValidator = list1.get(0).getCode();
				}

				log.info("Validar Codigo Repetido --->" + codigoValidator);

				/*
				 * Validar si ya existe Codigo de Banco Registrado; se opto en crear un atributo
				 * code para la entidad Banco ya que el id es autogenerado con un codigo Randon
				 */

				if (!codigoValidator.equals("") && codigoValidator.equals(bank.getCode())) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Codigo de Banco ya existe"));
				} else {
					
					listTypeBankAccountsFinal = this.validacionTypeBankAccounts(typeBankAccounts, bank);
					listCreditsFinal = this.validacionTypeCredits(typeCredits, bank);
					return this.bankSerivce.createBank(bank);
				}

			} catch (InterruptedException e) {
				log.info(e.toString());
				Thread.currentThread().interrupt();
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
			}

		} else {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Parametro de Entrada no obligatorio no completado, o "
							+ "parametro de Entrada enviado incorrectamente."));
		}

	}

	@PutMapping(value = "/{code}")
	public Mono<Bank> updateBank(@PathVariable String code, @RequestBody Bank bank) {

		// Condicion para validar que no se puede actualizar el ID

		if (bank.getId() != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El Atributo Id no puede actualizarse por ser un dato unico"));
		}

		if (bank.getCode() != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El Atributo Code no puede actualizarse por ser un dato unico"));
		}
		
		if (bank.getTypeBankAccounts() != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Por el momento no se puede Actualizar el Tipo de Cuenta Bancaria - typeBankAccounts"));
		}
		
		if (bank.getTypeCredits() != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Por el momento no se puede Actualizar el Tipo de Credito - typeCredits"));
		}

		Flux<Bank> list = this.bankSerivce.getAllBankByCode(code);

		List<Bank> list1 = new ArrayList<>();

		list.collectList().subscribe(list1::addAll);

		try {

			long temporizador = (7 * 1000);
			Thread.sleep(temporizador);

			if (list1.isEmpty()) {
				return Mono.error(
						new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "El Codigo de Banco no existe"));
			} else {
				Bank bankUpdate = this.validationUpdateBankRequest(list1, bank);
				return this.bankSerivce.createBank(bankUpdate);
			}

		} catch (InterruptedException e) {
			log.info(e.toString());
			Thread.currentThread().interrupt();
			return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
		}

	}
	
	@GetMapping(value = "/bankbycodeResponse/{code}")
	public Mono<ResponseEntity<BankResponse>> getbankbycodeResponse(@PathVariable String code) {

		try {

			Flux<BankResponse> customerflux = this.bankSerivce.getbankbycodeResponse(code);

			List<BankResponse> list1 = new ArrayList<>();

			customerflux.collectList().subscribe(list1::addAll);

			long temporizador = (3 * 1000);

			Thread.sleep(temporizador);

			if (list1.isEmpty()) {
				return null;

			} else {
				return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(list1.get(0)))
						.defaultIfEmpty(ResponseEntity.notFound().build());
			}

		} catch (InterruptedException e) {
			log.info(e.toString());
			Thread.currentThread().interrupt();
			return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteBankById(@PathVariable String id) {

		try {
			return this.bankSerivce.deleteBank(id).map(r -> ResponseEntity.ok().<Void>build())
					.defaultIfEmpty(ResponseEntity.notFound().build());

		} catch (Exception e) {
			log.info(e.toString());
			return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
		}

	}

	@GetMapping(value = "/{code}")
	public Mono<ResponseEntity<Bank>> getBankByCode(@PathVariable String code) {

		try {

			Flux<Bank> bankflux = this.bankSerivce.getAllBankByCode(code);

			List<Bank> list1 = new ArrayList<>();

			bankflux.collectList().subscribe(list1::addAll);

			long temporizador = (5 * 1000);

			Thread.sleep(temporizador);

			if (list1.isEmpty()) {
				return null;

			} else {
				return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(list1.get(0)))
						.defaultIfEmpty(ResponseEntity.notFound().build());
			}

		} catch (InterruptedException e) {
			log.info(e.toString());
			Thread.currentThread().interrupt();
			return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
		}
	}

	@GetMapping(value = "/BankbyId/{id}")
	public Mono<ResponseEntity<Bank>> getBankById(@PathVariable String id) {
		var bank = this.bankSerivce.getBankbyId(id);
		return bank.map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/get")
	public Mono<ResponseEntity<Flux<Bank>>> getAllBank() {
		Flux<Bank> list = this.bankSerivce.getAllBank();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(list));
	}

	@GetMapping(value = "/ProductsbyCode/{code}")
	public Mono<ResponseEntity<ClientProducts>> getProductsAccessToBank(@PathVariable String code){
 
		
		try {

			Flux<Bank> bankflux = this.bankSerivce.getAllBankByCode(code);

			List<Bank> list1 = new ArrayList<>();

			bankflux.collectList().subscribe(list1::addAll);

			long temporizador = (5 * 1000);

			Thread.sleep(temporizador);

			if (list1.isEmpty()) {
				return null;

			} else {
				
				ClientProducts listClientProducts = new ClientProducts();
				listClientProducts.setTypeBankAccounts(list1.get(0).getTypeBankAccounts());
				listClientProducts.setTypeCredits(list1.get(0).getTypeCredits());
				
				
				return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listClientProducts))
						.defaultIfEmpty(ResponseEntity.notFound().build());
			}

		} catch (InterruptedException e) {
			log.info(e.toString());
			Thread.currentThread().interrupt();
			return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
		}
		
	}
	 

	// Validaciones para Regitrar Banco

	public boolean validationRegisterBankRequest(Bank bank) {

		boolean validatorbank;

		if (bank.getCode() == null || bank.getCode().equals("")) {
			validatorbank = false;
		} else if (bank.getBankName() == null || bank.getBankName().equals("")) {
			validatorbank = false;
		} else if (bank.getDirectionMain() == null || bank.getDirectionMain().equals("")) {
			validatorbank = false;
		} else if (bank.getTypeBankAccounts() == null || bank.getTypeBankAccounts().get(0).getId() == null) {
			validatorbank = false;
		} else if (bank.getTypeCredits() == null || bank.getTypeCredits().get(0).getId() == null) {
			validatorbank = false;
		} else {
			validatorbank = true;
		}

		return validatorbank;
	}

	public Bank validationUpdateBankRequest(List<Bank> list1, Bank bank) {

		if (bank.getDirectionMain() == null || bank.getDirectionMain().equals("")) {
			bank.setDirectionMain(list1.get(0).getDirectionMain());
		} else {
			// se mantiene el dato enviado en el request
			log.info("Valor Direccion -->" + bank.getDirectionMain());
		}

		if (bank.getBankName() == null || bank.getBankName().equals("")) {
			bank.setBankName(list1.get(0).getBankName());
		} else {
			// se mantiene el dato enviado en el request
			log.info("Valor Name -->" + bank.getBankName());
		}

		if (bank.getId() == null || bank.getId().equals("")) {
			bank.setId(list1.get(0).getId());
		}

		if (bank.getCode() == null || bank.getCode().equals("")) {
			bank.setCode(list1.get(0).getCode());
		}
		 
		if (bank.getTypeBankAccounts() == null) {
			bank.setTypeBankAccounts(list1.get(0).getTypeBankAccounts());
		}
		
		if (bank.getTypeCredits() == null) {
			bank.setTypeCredits(list1.get(0).getTypeCredits());
		}

		return bank;
	}

	// Metodo para Mock
	
	public List<TypeBankAccounts> comunicationWebClientObtenerTypeBankAccountsMock() {

		List<TypeBankAccounts> typeBankAccounts = new ArrayList<>();

		typeBankAccounts.add(new TypeBankAccounts(1, "Ahorro", 0, 3));
		typeBankAccounts.add(new TypeBankAccounts(2, "Cuenta Corriente", 10, 0));
		typeBankAccounts.add(new TypeBankAccounts(3, "Plazo Fijo", 0, 0));

		
		return typeBankAccounts;

	}
	
	public List<TypeCredits> comunicationWebClientObtenerTypeCreditsMock() {

		List<TypeCredits> typeCredits = new ArrayList<>();

		typeCredits.add(new TypeCredits(1, "Credito Personal"));
		typeCredits.add(new TypeCredits(2, "Credito Empresarial"));
		typeCredits.add(new TypeCredits(3, "Tarjeta Credito Personal o Empresarial"));

		
		return typeCredits;

	}
	
	public List<TypeBankAccounts> validacionTypeBankAccounts(List<TypeBankAccounts> typeBankAccounts, Bank bank){
	
		for (int i = 0; i < typeBankAccounts.size(); i++) {
			String str = typeBankAccounts.get(i).getId().toString(); 

			log.info("Ver Codigo de Total de Tipo Cuentas ---> " + str);
			
			for (int a = 0; a < bank.getTypeBankAccounts().size(); a++) {
				
				if (str.equals(bank.getTypeBankAccounts().get(a).getId().toString())) {

					log.info("Ver Codigo de Request Bank --- > " + bank.getTypeBankAccounts().get(a).getId().toString()); 
					
					bank.getTypeBankAccounts().get(a).setId(typeBankAccounts.get(i).getId());
					bank.getTypeBankAccounts().get(a).setDescription(typeBankAccounts.get(i).getDescription());
					bank.getTypeBankAccounts().get(a).setCommission(typeBankAccounts.get(i).getCommission());
					bank.getTypeBankAccounts().get(a).setMaximumLimit(typeBankAccounts.get(i).getMaximumLimit());
					
					listTypeBankAccountsFinal.add(bank.getTypeBankAccounts().get(a));
				}
			}
		}
		
		log.info("Obtener List Final -->" + listTypeBankAccountsFinal);
		
		bank.setTypeBankAccounts(listTypeBankAccountsFinal);
		
		return listTypeBankAccountsFinal;
	}
	
	public List<TypeCredits> validacionTypeCredits(List<TypeCredits> typeCredits, Bank bank){
		
		for (int i = 0; i < typeCredits.size(); i++) {
			String str = typeCredits.get(i).getId().toString(); 

			log.info("Ver Codigo de Total de Tipo Creditos ---> " + str);
			
			for (int a = 0; a < bank.getTypeCredits().size(); a++) {
				
				if (str.equals(bank.getTypeCredits().get(a).getId().toString())) {

					log.info("Ver Codigo de Request Bank Credits --- > " + bank.getTypeCredits().get(a).getId().toString()); 
					
					bank.getTypeCredits().get(a).setId(typeCredits.get(i).getId());
					bank.getTypeCredits().get(a).setDescription(typeCredits.get(i).getDescription());
					listCreditsFinal.add(bank.getTypeCredits().get(a));
				}
			}
		}
		
		log.info("Obtener List Final  Credits -->" + listCreditsFinal);
		
		bank.setTypeCredits(listCreditsFinal);
		
		return listCreditsFinal;
	}
	
}
