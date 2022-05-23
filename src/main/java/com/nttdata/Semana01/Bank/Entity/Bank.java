package com.nttdata.Semana01.Bank.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	
}
