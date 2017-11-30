package com.puffinpowered.atm.service;

import com.puffinpowered.atm.domain.Note;

import java.math.BigDecimal;
import java.util.List;

public interface ATMChainService {
	List<Note> withDraw(BigDecimal amount);
}
