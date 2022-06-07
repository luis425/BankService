package com.nttdata.Semana01.Bank.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.Semana01.Bank.DTO.TypeBankAccounts;
import com.nttdata.Semana01.Bank.DTO.TypeCredits;
import com.nttdata.Semana01.Bank.Entity.Bank;
import com.nttdata.Semana01.Bank.Repository.BankRepository;
import com.nttdata.Semana01.Bank.response.BankResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service 
public class BankService {
 
	private WebClient typeBankAccountServiceClient = WebClient.builder().baseUrl("http://localhost:8082").build();
	
	private WebClient typeCreditserviceClient = WebClient.builder().baseUrl("http://localhost:8083").build();
	
	@Autowired
	BankRepository bankRepository; 
	
	public Flux<Bank> getAllBank() {
		return bankRepository.findAll();
	}

	public Flux<Bank> getAllBankByCode(String code) {
		return bankRepository.findAll().filter(x -> x.getCode().equals(code));
	}

	public Mono<Bank> createBank(Bank bank) {
		return bankRepository.save(bank);
	}

	public Mono<Bank> getBankbyId(String id) {
		return bankRepository.findById(id);
	}

	public Mono<Bank> deleteBank(String id) {
		return bankRepository.findById(id).flatMap(existsBank -> bankRepository
				.delete(existsBank).then(Mono.just(existsBank)));
	}
	
	public Flux<TypeBankAccounts> comunicationWebClientObtenerTypeBankAccounts() throws InterruptedException {

		Flux<TypeBankAccounts> typeBankAccountServiceClientResponse = typeBankAccountServiceClient.get()
				.uri("/typeBankAccounts").accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToFlux(TypeBankAccounts.class).log().doOnError(ex -> {
					throw new RuntimeException("the exception message is - " + ex.getMessage());
				});
		long temporizador = (5 * 1000);
		Thread.sleep(temporizador);
		
		return typeBankAccountServiceClientResponse;

	}
	
	public Flux<TypeCredits> comunicationWebClientObtenerTypeCredits() throws InterruptedException {

		Flux<TypeCredits> typeCreditsServiceClientResponse = typeCreditserviceClient.get()
				.uri("/typeCredits").accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToFlux(TypeCredits.class).log().doOnError(ex -> {
					throw new RuntimeException("the exception message is - " + ex.getMessage());
				});
		long temporizador = (5 * 1000);
		Thread.sleep(temporizador);
		
		return typeCreditsServiceClientResponse;

	}
	
	public Flux<BankResponse> getbankbycodeResponse(String code) {
		return bankRepository.findAll().filter(x -> x.getCode().equals(code))
				.map(bank -> BankResponse.builder()
						.id(bank.getId())
						.code(bank.getCode())
						.bankName(bank.getBankName())
						.directionMain(bank.getDirectionMain()) 
						.build());
	}
	
}