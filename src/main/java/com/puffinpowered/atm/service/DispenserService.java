package com.puffinpowered.atm.service;

import com.puffinpowered.atm.domain.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public interface DispenserService {


	void setNextChain(DispenserService nextChain);

	List<Note> dispense(int amount);
}
