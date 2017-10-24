package de.simonscholz.bot.telegram.handler;

import de.simonscholz.bot.telegram.api.Update;

public interface UpdateHandler {

	void handleUpdate(Update update);
}
