package com.puffinpowered.atm.service;

import com.puffinpowered.atm.domain.Note;
import com.puffinpowered.atm.enums.Denomination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("twenty")
public class Dollar20DispenserServiceImpl implements DispenserService {

	private static final Logger log = LoggerFactory.getLogger(Dollar20DispenserServiceImpl.class);

	private DispenserService chain;


	@Override
	public void setNextChain(DispenserService nextChain) {
		this.chain = nextChain;
	}

	@Override
	public List<Note> dispense(int amount) {
		List<Note> money = new ArrayList<Note>(1);

		Note dispensedAmount = new Note(Denomination.TWENTY, 0);
		int remainder = amount;
		if(amount >= 20){
			int num = amount/20;
			remainder = amount % 20;
			dispensedAmount.setNumber(num);
			log.info("Dispensing "+num+" 20$ notes");
			if(remainder !=0) log.info("There is a problem can't dispense "+remainder);
		}else{
		log.info("There is a problem can't dispense "+remainder);
		}
		money.add(dispensedAmount);
		return money;	}
}
