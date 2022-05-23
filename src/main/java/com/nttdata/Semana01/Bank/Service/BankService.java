package com.nttdata.Semana01.Bank.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.Semana01.Bank.Entity.Bank;
import com.nttdata.Semana01.Bank.Repository.BankRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service 
public class BankService {
 
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
}