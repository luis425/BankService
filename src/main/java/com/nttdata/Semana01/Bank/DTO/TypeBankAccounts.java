package com.nttdata.Semana01.Bank.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder; 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TypeBankAccounts {

	private Integer id; 
	
	private String description;
	 
	private Integer commission;
	 
	private Integer maximumLimit;
}
