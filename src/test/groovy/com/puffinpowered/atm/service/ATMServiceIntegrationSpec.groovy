package com.puffinpowered.atm.service

import com.puffinpowered.atm.AtmApplication
import com.puffinpowered.atm.domain.Cash
import com.puffinpowered.atm.domain.Note
import com.puffinpowered.atm.enums.Denomination
import com.puffinpowered.atm.repository.NoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by pshields on 10/11/2016.
 *
 * This is an integration test as we are loading some of the Spring context to get access to services, repositories and
 * an in memory database. In a full application we would have a separate integration properties file which would be used
 * to load the in memory database and run these tests every night.
 */
@ContextConfiguration(classes = [AtmApplication])
@ActiveProfiles("integrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ATMServiceIntegrationSpec extends Specification {


    @Autowired
    NoteRepository noteRepository

    @Autowired
    ATMService atmService



    def "Initialise machine"() {
        given: "An ATM service, and a cashbox full of notes"
        Cash cashBox = makeCashBox(10,12)
        when: "we load the cashbox"
        Boolean success = atmService.initialiseMachine(cashBox)
        then: "the ATM will contain the expected types and numbers of notes"
        assert atmService.notesAvailable(Denomination.FIFTY) == 10
        assert atmService.notesAvailable(Denomination.TWENTY) == 12
        assert success
    }

    def "It is not possible to initialise the machine twice"(){
        given: "The machine has already been initialised"
        Cash cashBox = makeCashBox(10,12)
        atmService.initialiseMachine(cashBox)
        when: "We try to initialise it again"
        Cash cashBox2 = makeCashBox(1,1)
        Boolean success = atmService.initialiseMachine(cashBox2)
        then: "the second initialisation doesn't work"
        assert atmService.notesAvailable(Denomination.FIFTY) == 10
        assert atmService.notesAvailable(Denomination.TWENTY) == 12
        assert success  == false
    }

    def "Available Funds are discoverable" (){
        given:"The machine has already been initialised"
        Cash cashBox = makeCashBox(1,1)
        atmService.initialiseMachine(cashBox)
        when: "we check the available funds"
        BigDecimal availableFunds = atmService.availableFunds()
        then: "the amount is correct"
        availableFunds == BigDecimal.valueOf(70.0)
    }

    def "We don't have enough money for withdrawal request"(){
        given: "The machine has already been initialised"
        Cash cashBox = makeCashBox(1,1)
        Boolean success1 = atmService.initialiseMachine(cashBox)
        when: "We try to withdraw more money than is left"
        BigDecimal withdrawalRequest = new BigDecimal(300)
        List<Note> result = atmService.withDraw(withdrawalRequest)
        then: "no money is dispensed"
        assert atmService.notesAvailable(Denomination.FIFTY) == 1
        assert atmService.notesAvailable(Denomination.TWENTY) == 1
        assert result.get(0).number  == 0
        assert result.get(1).number  == 0
    }

    def "Withdrawal amount must be divisible by 10"(){
        given: "The machine has already been initialised"
        Cash cashBox = makeCashBox(1,1)
        Boolean success1 = atmService.initialiseMachine(cashBox)
        when: "We try to withdraw more money than is left"
        BigDecimal withdrawalRequest = new BigDecimal(4)
        List<Note> result = atmService.withDraw(withdrawalRequest)
        then: "no money is dispensed"
        assert atmService.notesAvailable(Denomination.FIFTY) == 1
        assert atmService.notesAvailable(Denomination.TWENTY) == 1
        assert result.get(0).number  == 0
        assert result.get(1).number  == 0
    }

    def "We can dispense multiples of 20"(){
        given: "We have enough 20s and the machine has been initialised"
        Cash cashBox = makeCashBox(0,20)
        Boolean success= atmService.initialiseMachine(cashBox)
        expect: "We will dispense the correct amounts"
        List<Note> result = atmService.withDraw(a)
        assert result.get(1).number == b
        where:
        a << [20, 40, 60, 80, 100, 120, 140, 160, 180, 200]
        b << [1,2,3,4,5,6,7,8,9,10]
    }

    def "We can dispense multiples of 50"(){
        given: "We have enough 50s and the machine has been initialised"
        Cash cashBox = makeCashBox(20,20)
        Boolean success= atmService.initialiseMachine(cashBox)
        expect: "We will dispense the correct amounts"
        List<Note> result = atmService.withDraw(a)
        assert result.get(0).number == b
        where:
        a << [50, 100, 150, 200, 250, 300, 350, 400, 450, 500]
        b << [1,2,3,4,5,6,7,8,9,10]
    }
     @Unroll
    def "We can dispense multiples of 70"(){
        given: "We have enough 50s and 20s and the machine has been initialised"
        Cash cashBox = makeCashBox(20,24)
        Boolean success= atmService.initialiseMachine(cashBox)
        expect: "We will dispense the correct amounts"
        List<Note> result = atmService.withDraw(d)
        assert result.get(0).number == e
        assert result.get(1).number == f
        where:
        d << [70, 140, 210, 280, 340, 410, 480]
        e << [1, 0, 3, 0, 0, 1, 0]
        f << [1, 7, 3, 14, 17, 17, 24]
    }

    def "We can dispense 110"(){
        given: "The machine has already been initialised"
            Cash cashBox = makeCashBox(20,20)
            Boolean success1 = atmService.initialiseMachine(cashBox)
        when: "We try to withdraw 110 dollars"
            BigDecimal withdrawalRequest = new BigDecimal(110)
            List<Note> result = atmService.withDraw(withdrawalRequest)
        then: "the correct money is dispensed"
            assert result.get(0).number == 1
            assert result.get(1).number == 3
    }

    def "We can dispense 200 when we must use both denominations)"(){
        given: "The machine has already been initialised"
        Cash cashBox = makeCashBox(3,8)
        Boolean success = atmService.initialiseMachine(cashBox)
        when: "We try to withdraw 110 dollars"
        BigDecimal withdrawalRequest = new BigDecimal(200)
        List<Note> result = atmService.withDraw(withdrawalRequest)
        then: "the correct money is dispensed"
        assert result.get(0).number == 2
        assert result.get(1).number == 5
    }




    def makeCashBox(int numberOfFifties, int numberOfTwenties) {
        Cash cashBox = new Cash()
        List<Note> money = new ArrayList<Note>(2)
        Note twenties = new Note(Denomination.TWENTY, numberOfTwenties)
        Note fifties = new Note(Denomination.FIFTY, numberOfFifties)
        money.add(twenties)
        money.add(fifties)
        return money
    }


}