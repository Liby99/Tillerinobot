package tillerino.tillerinobot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import tillerino.tillerinobot.rest.BeatmapInfoService;
import tillerino.tillerinobot.rest.BotInfoService;
import tillerino.tillerinobot.rest.RecommendationHistoryService;

/**
 * @author Tillerino
 */
public class BotAPIServer extends Application {
	public IRCBot bot;
	public BotBackend backend;
	
	public BotAPIServer(BotBackend backend) {
		this.backend = backend;
	}

	BotInfoService botInfo = new BotInfoService(this);
	RecommendationHistoryService history = new RecommendationHistoryService(this);
	BeatmapInfoService beatmapInfo = new BeatmapInfoService(this);
	
	@Override
	public Set<Object> getSingletons() {
		return new HashSet<Object>(Arrays.asList(botInfo, history, beatmapInfo));
	}
	
	public void setBot(IRCBot bot) {
		this.bot = bot;
	}

	public static void throwUnautorized(boolean authorized) throws WebApplicationException {
		if(authorized)
			return;
		
		throw exceptionFor(Status.UNAUTHORIZED, "Your key is not authorized for this method.");
	}
	
	public static WebApplicationException getBadGateway() {
		return exceptionFor(Status.BAD_GATEWAY, "Communication with the osu API server failed.");
	}
	
	public static WebApplicationException getNotFound(String message) {
		return exceptionFor(Status.NOT_FOUND, message);
	}

	public static WebApplicationException getUserMessage(UserException exception) {
		return getNotFound(exception.getMessage());
	}

	public static WebApplicationException exceptionFor(Status status, String message) {
		return new WebApplicationException(Response.status(status).entity(message).build());
	}

	public static WebApplicationException getInterrupted() {
		return exceptionFor(Status.SERVICE_UNAVAILABLE, "The server is being shutdown for maintenance");
	}
}
