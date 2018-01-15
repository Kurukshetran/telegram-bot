package de.simonscholz.bot.telegram.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.simonscholz.bot.telegram.entities.Query;

@RepositoryRestResource(collectionResourceRel="queries", path="queries")
public interface QueryRepository extends JpaRepository<Query, Long>{

}
