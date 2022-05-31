package com.nttdata.Semana01.Bank.Entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.Semana01.Bank.DTO.TypeBankAccounts;
import com.nttdata.Semana01.Bank.DTO.TypeCredits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Document
@Data
@Builder
public class Bank {
 
	
	@Id
	private String id;
	 
	private String code;
	
	private String bankName;
	
	private String directionMain;
	 
	private List<TypeBankAccounts> typeBankAccounts;
	
	private List<TypeCredits> typeCredits;
	
	
}
