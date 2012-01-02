package de.benjaminborbe.bookmark.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.tools.dao.DaoBase;
import de.benjaminborbe.tools.util.IdGenerator;

@Singleton
public class BookmarkDaoImpl extends DaoBase<BookmarkBean> implements BookmarkDao {

	private static final String DEFAULT_DESCRIPTION = "-";

	@Inject
	public BookmarkDaoImpl(final Logger logger, final IdGenerator idGenerator, final Provider<BookmarkBean> provider) {
		super(logger, idGenerator, provider);
	}

	@Override
	protected void init() {
		// internal
		save(createBookmark("/bb/bookmark", "Local - BB - Bookmarks", Arrays.asList("bookmark", "lesezeichen")));
		save(createBookmark("/bb/worktime", "Local - BB - Worktimes"));
		save(createBookmark("/bb/util/passwordGenerator", "Local - BB - PasswordGenerator"));
		save(createBookmark("/bb/monitoring", "Local - BB - Monitoring"));
		save(createBookmark("/bb/gwt/Home.html", "Local - BB - GWT"));
		save(createBookmark("/bb/search", "Local - BB - Search"));
		save(createBookmark("/bb/search/components", "Local - BB - Search Components"));

		// extern
		save(createBookmark("https://console.aws.amazon.com/ec2/home", "Amazon EC2"));
		save(createBookmark("http://kleinanzeigen.ebay.de/", "Ebay Kleinanzeigen"));
		save(createBookmark("http://confluence.rocketnews.de", "Rocketnews - Confluence - Wiki",
				Arrays.asList("wiki", "confluence")));
		save(createBookmark("http://www.harteslicht.com", "Harteslicht",
				Arrays.asList("foto", "photo", "photography", "fotografie")));
		save(createBookmark("http://www.benjamin-borbe.de", "Benjamin Borbe",
				Arrays.asList("foto", "photo", "photography", "fotografie", "portfolio")));
		save(createBookmark("http://tomtom.de/gettingstarted", "TomTom"));
		save(createBookmark("http://www.guenstiger.de", "Guenstiger", Arrays.asList("search")));
		save(createBookmark("http://geizhals.at/deutschland/", "Geizhals", Arrays.asList("search")));
		save(createBookmark("http://www.google.de/?hl=en", "Google"));
		save(createBookmark("http://www.audible.de/", "Audible - Hörbücher",
				Arrays.asList("Hörbücher", "Hoerbuecher", "shop")));
		save(createBookmark("http://www.dwitte.de/", "Dennis Witte", Arrays.asList("Dennis Witte", "dwitte")));
		save(createBookmark("http://wuhrsteinalm.de/", "Wuhrsteinalm"));
		save(createBookmark("http://www.postbank.de/", "Postbank", Arrays.asList("bank")));
		save(createBookmark("https://banking.dkb.de/", "DKB", Arrays.asList("bank")));
		save(createBookmark("http://www.tagesschau.de/", "Tagesschau", Arrays.asList("Tagesschau")));
		save(createBookmark("http://www.tagesschau.de/100sekunden/", "Tagesschau in 100 Sekunden",
				Arrays.asList("Tagesschau")));

		// local
		save(createBookmark("http://localhost:8180/manager/html/list", "Local - Tomcat Manager"));
		save(createBookmark("http://phpmyadmin/", "Local - phpMyAdmin"));
		save(createBookmark("http://0.0.0.0:8161/admin/queues.jsp", "Local - ActiveMQ - JMS"));

		// 20ft devel
		save(createBookmark("/bb/twentyfeetperformance", "Twentyfeet - Devel - Performance"));
		save(createBookmark("http://localhost:8180/app/", "Twentyfeet - Devel"));
		save(createBookmark("http://localhost:8180/app/admin", "Twentyfeet - Devel - Admin"));
		save(createBookmark("http://localhost:8180/app/?log_level=DEBUG", "Twentyfeet - Devel - App with Debug"));
		save(createBookmark("http://127.0.0.1:8888/app/Home.html?gwt.codesvr=127.0.0.1:9997",
				"Twentyfeet - Devel - App in Developermode"));

		// 20ft live
		save(createBookmark("https://www.twentyfeet.com/", "Twentyfeet - Live"));
		save(createBookmark("https://www.twentyfeet.com/admin/queues.jsp", "Twentyfeet - Live - ActiveMQ - JMS"));
		save(createBookmark("https://central.twentyfeet.com/phpmyadmin/", "Twentyfeet - Live - phpMyadmin"));
		save(createBookmark("https://kunden.seibert-media.net/display/20ft", "Twentyfeet - Live - Wiki"));

		// 20ft test
		save(createBookmark("https://test.twentyfeet.com/", "Twentyfeet - Test"));
		save(createBookmark("https://test.twentyfeet.com/admin/queues.jsp", "Twentyfeet - Test - ActiveMQ - JMS"));

		// seibert-media
		save(createBookmark("https://timetracker.rp.seibert-media.net/", "Seibert-Media - Timetracker"));
		save(createBookmark("https://confluence.rp.seibert-media.net/", "Seibert-Media - Confluence - Wiki"));
		save(createBookmark("https://hudson.rp.seibert-media.net/", "Seibert-Media - Hudson / Jenkins"));
		save(createBookmark("https://micro.rp.seibert-media.net", "Seibert-Media - Microblog"));
		save(createBookmark("http://nexus.rp.seibert-media.net/", "Seibert-Media - Nexus"));
		save(createBookmark("https://zimbra.rp.seibert-media.net/", "Seibert-Media - Zimbra"));

		// Movie
		save(createBookmark("http://www.cinestar.de/de/kino/mainz-cinestar/", "Movie - Kino - Mainz - Cinestar"));
		save(createBookmark("http://www.filmstarts.de/", "Movie - Review - Filmstarts"));
		save(createBookmark("http://rogerebert.suntimes.com/", "Movie - Review - Roger Ebert"));
		save(createBookmark("http://imdb.com/", "Movie - Review - Imdb"));
	}

	protected BookmarkBean createBookmark(final String url, final String name) {
		return createBookmark(url, name, DEFAULT_DESCRIPTION, new ArrayList<String>());
	}

	protected BookmarkBean createBookmark(final String url, final String name, final List<String> keywords) {
		return createBookmark(url, name, DEFAULT_DESCRIPTION, keywords);
	}

	protected BookmarkBean createBookmark(final String url, final String name, final String description,
			final List<String> keywords) {
		final BookmarkBean bookmark = create();
		bookmark.setUrl(url);
		bookmark.setName(name);
		bookmark.setDescription(description);
		bookmark.setKeywords(keywords);
		return bookmark;
	}

}
