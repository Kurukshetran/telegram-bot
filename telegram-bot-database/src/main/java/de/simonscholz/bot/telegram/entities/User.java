package de.simonscholz.bot.telegram.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long telegramId;
	private boolean is_bot;
	private String first_name;
	private String last_name;
	private String username;
	private String languageCode;
	
	@OneToMany
	private List<Query> queries;
	
	public void setId(long id) {
		// The id should not be set by clients, there this method is not
		// generated by lombok
	}
}
