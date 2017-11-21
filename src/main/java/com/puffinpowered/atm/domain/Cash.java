package com.puffinpowered.atm.domain;

import java.beans.Transient;
import java.util.List;


public class Cash {

	List<Note> money;

	public Cash(List<Note> money) {
		this.money = money;
	}

	public List<Note> getMoney() {
		return money;
	}

	public void setMoney(List<Note> money) {
		this.money = money;
	}
}
