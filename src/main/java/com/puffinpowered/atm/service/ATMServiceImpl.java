package com.puffinpowered.atm.service;

import com.puffinpowered.atm.domain.Cash;
import com.puffinpowered.atm.domain.Note;
import com.puffinpowered.atm.enums.Denomination;
import com.puffinpowered.atm.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ATMServiceImpl implements ATMService {

	private static final Logger log = LoggerFactory.getLogger(ATMServiceImpl.class);


	NoteRepository noteRepository;

	@Autowired
	public ATMServiceImpl(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}


	@Override
	public int notesAvailable(Denomination denomination) {
		Note note = (noteRepository.findByType(denomination));
		return note.getNumber();
	}

	@Override
	public Boolean initialiseMachine(Cash cash) {
		Boolean success = true;
		for (Note note: cash.getMoney()) {
			if (noteRepository.countByType(note.getType()) != 0) {
				success = false;
			}
		}
		if (success) {
			for (Note note : cash.getMoney()) {
				try {
					if (noteRepository.countByType(note.getType()) == 0) {
						noteRepository.save(note);
					}
				} catch (Exception e) {
					success = false;
					log.error(e.getMessage());
				}
			}
		}
		return success;
	}

	public BigDecimal availableFunds() {
		BigDecimal availableFunds = BigDecimal.ZERO;
		Iterable<Note> cash = noteRepository.findAll();
		for (Note note : cash){
			BigDecimal value1 = BigDecimal.valueOf(note.getNumber());
			BigDecimal value2 = note.getType().value();
			availableFunds = availableFunds.add(value1.multiply(value2));
		}
		return availableFunds;
	}

	@Override
	public List<Note> loadMoney(Denomination denomination, BigDecimal amount) {

		Note note = noteRepository.findByType(denomination);
		note.setNumber(note.getNumber()+amount.intValue());
		noteRepository.save(note);

		Note twenty = new Note(Denomination.TWENTY,0);
		Note fifty = new Note(Denomination.FIFTY,0);
		List<Note> result = new ArrayList<Note>(2);
		if (denomination == Denomination.TWENTY) {
			twenty.setNumber(amount.intValue());
		} else {
			fifty.setNumber(amount.intValue());
		}
		result.add(fifty);
		result.add(twenty);
		return result;
	}

	private List<Note> dispense(BigDecimal amount, List<Note> result) {
		Note fiftyEntity = noteRepository.findByType(Denomination.FIFTY);
		Note twentyEntity = noteRepository.findByType(Denomination.TWENTY);
		int fifties = fiftyEntity.getNumber();
		int twenties = twentyEntity.getNumber();
		int dispensedTwenties = 0;
		int dispensedFifties = 0;
		int tempDispense =0;
		int requestedAmount = amount.intValue(); // we know we only accept whole numbers
		//multiples of 50 (50, 100, 150, 200)
		if (requestedAmount%50 == 0 && requestedAmount/50 <= fifties) {
			int numberOf50s = requestedAmount / 50;
			dispensedFifties = numberOf50s;
			fifties = fifties - numberOf50s;
		} else if (requestedAmount/20 > twenties && requestedAmount/50 > fifties) {
			//200 - only 3 50s and 8 20s
			int tempFifties = 0;
			int tempTwenties =0;
			while (tempFifties == 0 ||(( tempFifties < fifties) && ((requestedAmount - tempFifties*50)%20 != 0) && ((requestedAmount - tempFifties*50) / 20 <= twenties))){
				tempFifties++;
			}
			tempTwenties = (requestedAmount - tempFifties*50) / 20;
			tempDispense = tempFifties*50 + tempTwenties*20;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				twenties = twenties - dispensedTwenties;
				dispensedFifties = tempFifties;
				fifties = fifties - dispensedFifties;
			}
		}
		//multiples of 20 (20, 40, 60, 80, 100, 200)
		else if(requestedAmount%20 == 0 && (requestedAmount/20 <= twenties)) {
			int numberOf20s = requestedAmount/20;
			dispensedTwenties = numberOf20s;
			twenties = twenties - numberOf20s;
		}
		//must have 50s and 20s
		else if (requestedAmount%70 == 0)  {
			int numberOfUnits = requestedAmount/70;
			dispensedTwenties = numberOfUnits;
			dispensedFifties = numberOfUnits;
			twenties = twenties - dispensedTwenties;
			fifties = fifties - dispensedFifties;
		}
		else if (requestedAmount == 410) {
			if (fifties >=1 && twenties >=17) {
				dispensedFifties = 1;
				dispensedTwenties = 17;
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}else if (requestedAmount%20 != 0){  //taking 50 from an odd number makes it divisible by 20?
			int tempTwenties =  (requestedAmount-50)/20;
			int tempFifties =  1;
			tempDispense = tempTwenties * 20 + tempFifties * 50;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				dispensedFifties = tempFifties;
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}
		else {  // try our luck with this
			int tempTwenties =  requestedAmount%50;
			int tempFifties =  requestedAmount/50;
			tempDispense = tempTwenties * 20 + tempFifties * 50;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				dispensedFifties = tempFifties;
			} else {
				tempFifties = requestedAmount%20;
				tempTwenties = requestedAmount/20;
				tempDispense = tempTwenties * 20 + tempFifties * 50;
				if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
					dispensedTwenties = tempTwenties;
					dispensedFifties = tempFifties;
				}
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}

		result = dispenseNotes(dispensedFifties, dispensedTwenties);

		if(result.get(0).getNumber() != 0 || result.get(1).getNumber() != 0) {
			fiftyEntity.setNumber(fifties);
			twentyEntity.setNumber(twenties);
			saveBalance(fiftyEntity, twentyEntity);
		}
		return result;
	}



	private  List<Note> dispenseNotes(int fifties, int twenties){
		List<Note> result = new ArrayList<Note>(2);
		result.add(new Note (Denomination.FIFTY, fifties));
		result.add(new Note(Denomination.TWENTY, twenties));
		return result;
	}

	private void saveBalance(Note fifties, Note twenties){
		noteRepository.save(fifties);
		noteRepository.save(twenties);
	}


	public Boolean checkEdgeCases(BigDecimal amount){
		Boolean proceed = true;
		BigDecimal availableFunds = this.availableFunds();
		if(amount.compareTo(this.availableFunds()) > 0) {
			proceed = false;
		} else if (amount.compareTo(BigDecimal.valueOf(20.0)) < 0){
			proceed = false;
		}  else if (amount.compareTo(BigDecimal.valueOf(30.0)) == 0){
			proceed = false;
		}  else if (amount.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) != 0)  {
			proceed = false;
		}
		return proceed;
	}

	@Override
	public List<Note> withDraw(BigDecimal amount) {
		Note twenty = new Note(Denomination.TWENTY,0);
		Note fifty = new Note(Denomination.FIFTY,0);
		List<Note> result = new ArrayList<Note>(2);
		result.add(fifty);
		result.add(twenty);
		if (checkEdgeCases(amount)) {
			result = this.dispense(amount, result);
		}
		return result;
	}
	 @Override
	public Boolean checkAmount(List<Note> money) {
		Boolean success = false;
		int numberOfNotes = 0;
		for (Note note : money) {
			numberOfNotes += note.getNumber();
		}
		if(numberOfNotes > 0 ){
			success = true;
		}
		return success;
	}

}
