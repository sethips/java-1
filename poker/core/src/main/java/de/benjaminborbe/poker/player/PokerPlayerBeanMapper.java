package de.benjaminborbe.poker.player;

import com.google.inject.Provider;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.poker.api.PokerCardIdentifier;
import de.benjaminborbe.poker.api.PokerGameIdentifier;
import de.benjaminborbe.poker.api.PokerPlayerIdentifier;
import de.benjaminborbe.poker.util.MapperPokerCardIdentifierList;
import de.benjaminborbe.poker.util.MapperPokerGameIdentifier;
import de.benjaminborbe.poker.util.MapperPokerPlayerIdentifier;
import de.benjaminborbe.poker.util.MapperUserIdentifierCollection;
import de.benjaminborbe.tools.mapper.MapperCalendar;
import de.benjaminborbe.tools.mapper.MapperLong;
import de.benjaminborbe.tools.mapper.MapperString;
import de.benjaminborbe.tools.mapper.mapobject.MapObjectMapperAdapter;
import de.benjaminborbe.tools.mapper.stringobject.StringObjectMapper;
import de.benjaminborbe.tools.mapper.stringobject.StringObjectMapperAdapter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

@Singleton
public class PokerPlayerBeanMapper extends MapObjectMapperAdapter<PokerPlayerBean> {

	public static final String BET = "bet";

	public static final String ID = "id";

	public static final String NAME = "name";

	public static final String CREATED = "created";

	public static final String MODIFIED = "modified";

	public static final String AMOUNT = "amount";

	public static final String GAME_ID = "game";

	public static final String CARDS = "cards";

	public static final String TOKEN = "token";

	public static final String OWNERS = "owners";

	public static final String SCORE = "score";

	@Inject
	public PokerPlayerBeanMapper(
		final Provider<PokerPlayerBean> provider,
		final MapperUserIdentifierCollection mapperUserIdentifierCollection,
		final MapperPokerPlayerIdentifier mapperPokerPlayerIdentifier,
		final MapperPokerGameIdentifier mapperPokerGameIdentifier,
		final MapperCalendar mapperCalendar,
		final MapperString mapperString,
		final MapperLong mapperLong,
		final MapperPokerCardIdentifierList mapperPokerCardIdentifierList
	) {
		super(provider, buildMappings(mapperPokerPlayerIdentifier, mapperPokerGameIdentifier, mapperCalendar, mapperString, mapperLong, mapperPokerCardIdentifierList,
			mapperUserIdentifierCollection));
	}

	private static Collection<StringObjectMapper<PokerPlayerBean>> buildMappings(
		final MapperPokerPlayerIdentifier mapperPokerPlayerIdentifier,
		final MapperPokerGameIdentifier mapperPokerGameIdentifier,
		final MapperCalendar mapperCalendar,
		final MapperString mapperString,
		final MapperLong mapperLong,
		final MapperPokerCardIdentifierList mapperPokerCardIdentifierList,
		final MapperUserIdentifierCollection mapperUserIdentifierCollection
	) {
		final List<StringObjectMapper<PokerPlayerBean>> result = new ArrayList<StringObjectMapper<PokerPlayerBean>>();
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, PokerPlayerIdentifier>(ID, mapperPokerPlayerIdentifier));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, PokerGameIdentifier>(GAME_ID, mapperPokerGameIdentifier));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, String>(NAME, mapperString));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, String>(TOKEN, mapperString));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Long>(AMOUNT, mapperLong));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Long>(SCORE, mapperLong));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Collection<UserIdentifier>>(OWNERS, mapperUserIdentifierCollection));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Long>(BET, mapperLong));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, List<PokerCardIdentifier>>(CARDS, mapperPokerCardIdentifierList));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Calendar>(CREATED, mapperCalendar));
		result.add(new StringObjectMapperAdapter<PokerPlayerBean, Calendar>(MODIFIED, mapperCalendar));
		return result;
	}
}
