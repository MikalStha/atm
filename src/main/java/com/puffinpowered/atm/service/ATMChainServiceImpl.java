package com.puffinpowered.atm.service;

import com.puffinpowered.atm.domain.Note;
import com.puffinpowered.atm.enums.Denomination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ATMChainServiceImpl implements ATMChainService {

	private DispenserService fiftyDispenser;

	private DispenserService twentyDispenser;

	@Autowired
	public ATMChainServiceImpl(@Qualifier("fifty") DispenserService fiftyDispenser, @Qualifier("twenty") DispenserService twentyDispenser) {
		this.fiftyDispenser = fiftyDispenser;
		this.twentyDispenser = twentyDispenser;
		this.fiftyDispenser.setNextChain(twentyDispenser);
	}


	@Override
	public List<Note> withDraw(BigDecimal amount) {
		return this.fiftyDispenser.dispense(amount.intValue());
	}
}
