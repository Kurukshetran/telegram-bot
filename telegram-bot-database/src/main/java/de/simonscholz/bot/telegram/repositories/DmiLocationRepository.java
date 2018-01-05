package de.simonscholz.bot.telegram.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.simonscholz.bot.telegram.entities.DmiLocation;

public interface DmiLocationRepository extends JpaRepository<DmiLocation, Long> {

	Optional<DmiLocation> findByLabelContaining(String label);
}
