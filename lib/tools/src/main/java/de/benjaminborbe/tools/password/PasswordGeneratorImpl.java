package de.benjaminborbe.tools.password;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Singleton
public class PasswordGeneratorImpl implements PasswordGenerator {

	private final Logger logger;

	@Inject
	public PasswordGeneratorImpl(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public String generatePassword(final int length, final PasswordCharacter... characters) {
		return generatePassword(length, Arrays.asList(characters));
	}

	protected List<Character> combine(final Collection<PasswordCharacter> characters) {
		final List<Character> cs = new ArrayList<Character>();
		for (final PasswordCharacter character : characters) {
			cs.addAll(toCharacter(character.getCharacters()));
		}
		return cs;
	}

	protected Collection<Character> toCharacter(final char... characters) {
		final Character[] cs = ArrayUtils.toObject(characters);
		return Arrays.asList(cs);
	}

	@Override
	public String generatePassword(final int length, final Collection<PasswordCharacter> characters) {
		if (characters.isEmpty()) {
			throw new IllegalArgumentException("at least one PasswordCharacter needed");
		}
		logger.trace("generatePassword with lenght: " + length);
		final List<Character> cs = combine(characters);
		final StringBuffer result = new StringBuffer();
		final Random r = new Random();
		for (int i = 0; i < length; ++i) {
			result.append(cs.get(r.nextInt(cs.size())));
		}
		return result.toString();
	}
}
