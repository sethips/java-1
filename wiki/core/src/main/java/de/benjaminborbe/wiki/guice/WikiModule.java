package de.benjaminborbe.wiki.guice;

import com.google.inject.AbstractModule;
import de.benjaminborbe.tools.log.LoggerSlf4Provider;
import de.benjaminborbe.wiki.api.WikiService;
import de.benjaminborbe.wiki.dao.WikiPageDao;
import de.benjaminborbe.wiki.dao.WikiPageDaoImpl;
import de.benjaminborbe.wiki.dao.WikiSpaceDao;
import de.benjaminborbe.wiki.dao.WikiSpaceDaoImpl;
import de.benjaminborbe.wiki.render.WikiRendererFactory;
import de.benjaminborbe.wiki.render.WikiRendererFactoryImpl;
import de.benjaminborbe.wiki.service.WikiServiceImpl;
import org.slf4j.Logger;

import javax.inject.Singleton;

public class WikiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WikiRendererFactory.class).to(WikiRendererFactoryImpl.class).in(Singleton.class);
		bind(WikiSpaceDao.class).to(WikiSpaceDaoImpl.class).in(Singleton.class);
		bind(WikiPageDao.class).to(WikiPageDaoImpl.class).in(Singleton.class);
		bind(WikiService.class).to(WikiServiceImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);

		requestStaticInjection(WikiValidatorLinker.class);
	}
}
