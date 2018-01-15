package de.simonscholz.bot.telegram.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.simonscholz.bot.telegram.entities.DmiLocation;

@RepositoryRestResource(collectionResourceRel="locations", path="locations")
public interface DmiLocationRepository extends JpaRepository<DmiLocation, Long> {

	Optional<DmiLocation> findByLabelContaining(String label);
}
