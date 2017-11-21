package com.puffinpowered.atm.controller;

import com.puffinpowered.atm.domain.Cash;
import com.puffinpowered.atm.domain.Money;
import com.puffinpowered.atm.domain.Note;
import com.puffinpowered.atm.enums.Denomination;
import com.puffinpowered.atm.service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller is for machine to machine communication
 */
@RestController
@RequestMapping("/api")
public class ApiController {
	ATMService atmService;



	@Autowired
	public ApiController(ATMService atmService) {
		this.atmService = atmService;
	}

	@RequestMapping(value="/initialise", method = RequestMethod.POST)
	public ResponseEntity<String> initialiseAtm(@RequestBody Money money){
		String message = "Machine has already been initialised, new request ignored";
		HttpStatus status = HttpStatus.PRECONDITION_FAILED;
		List notes = new ArrayList<Note>(2);
		Note fifties = new Note(Denomination.FIFTY, money.getFifties());
		Note twenties = new Note(Denomination.TWENTY, money.getTwenties());
		notes.add(fifties);
		notes.add(twenties);
		Cash cash = new Cash(notes);
		Boolean success = atmService.initialiseMachine(cash);
		if (success){
			message = "Machine has been initialised with " +fifties.getNumber()+ " fifty dollar notes and "+twenties.getNumber()+" twenty dollar notes";
			status = HttpStatus.OK;
		}
		return new ResponseEntity<String>(message, status );
	}

	@RequestMapping(value="/withdraw/{amount}", method = RequestMethod.GET)
	public ResponseEntity<Cash> withDrawMoney(@PathVariable BigDecimal amount) {
		String message = "Thank-you for your patronage, please find your bank notes below.";
		List<Note> money = atmService.withDraw(amount);
		Cash cash = new Cash(money);
		HttpStatus status = HttpStatus.PRECONDITION_FAILED;
		Boolean amountWasDispensed = atmService.checkAmount(money);
		if (amountWasDispensed) {
			status = HttpStatus.OK;
		}
			return new ResponseEntity<Cash>(cash, status);
	}

	@RequestMapping(value = "/load/{type}/{amount}", method = RequestMethod.PUT)
	public ResponseEntity<Cash> loadMoney(@PathVariable int type,@PathVariable BigDecimal amount, Model model){
		Denomination denomination = Denomination.FIFTY;
		if (type == 20){
			denomination = Denomination.TWENTY;
		}
		List<Note> money = atmService.loadMoney(denomination,amount);
		Cash cash = new Cash(money);
		return new ResponseEntity<Cash>(cash, HttpStatus.OK);
	}

	@RequestMapping(value = "/check/{type}", method = RequestMethod.GET)
	public ResponseEntity<Integer> checkNumberOfNotesAvailable(@PathVariable int type ) {
		Denomination denomination = Denomination.FIFTY;
		if (type == 20){
			denomination = Denomination.TWENTY;
		}
		int numberOfNotes = atmService.notesAvailable(denomination);
		return  new ResponseEntity<Integer>(numberOfNotes, HttpStatus.OK);
	}




	/**
	 * Sample JSON Money format required
	 * @return  Money object as JSON
	 */
	 @RequestMapping(value="/help/money", method=RequestMethod.GET)
	public Money moneyJSON(){
		Money money = new Money();
		money.setFifties(10);
		money.setTwenties(10);
		return money;
	 }

}
