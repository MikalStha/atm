package com.puffinpowered.atm.domain;

import org.springframework.stereotype.Component;

/**
 * This is a command object to back the initialisation form
 */
public class Money {
	int twenties;
	int fifties;

	public Money(int twenties, int fifties) {
		this.twenties = twenties;
		this.fifties = fifties;
	}

	public Money() {
	}

	public int getTwenties() {
		return twenties;
	}

	public void setTwenties(int twenties) {
		this.twenties = twenties;
	}

	public int getFifties() {
		return fifties;
	}

	public void setFifties(int fifties) {
		this.fifties = fifties;
	}
}
