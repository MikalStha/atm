package com.puffinpowered.atm.repository;

import com.puffinpowered.atm.domain.Note;
import com.puffinpowered.atm.enums.Denomination;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {

	Note findByType(Denomination denomination);

	int countByType(Denomination denomination);
}
