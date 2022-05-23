package com.nttdata.Semana01.Bank.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.Semana01.Bank.Entity.Bank;

@Repository
public interface BankRepository extends ReactiveCrudRepository<Bank, String>{

}
