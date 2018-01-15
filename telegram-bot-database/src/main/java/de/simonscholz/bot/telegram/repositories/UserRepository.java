package de.simonscholz.bot.telegram.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.simonscholz.bot.telegram.entities.User;

@RepositoryRestResource(collectionResourceRel="user", path="user")
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findUserByTelegramId(long id);
}
