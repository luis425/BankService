package com.nttdata.Semana01.Bank.DTO;

import java.util.List;

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
public class ClientProducts {

	private List<TypeBankAccounts> typeBankAccounts;
	
	private List<TypeCredits> typeCredits;
}
