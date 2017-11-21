package com.puffinpowered.atm.enums;

import java.math.BigDecimal;

public enum Denomination {
	TWENTY(new BigDecimal(20)),
	FIFTY(new BigDecimal(50));

	private BigDecimal value;

	Denomination(BigDecimal amount)  {
		value = amount;
	}
	public BigDecimal value() {
		return value;
	  }
}
