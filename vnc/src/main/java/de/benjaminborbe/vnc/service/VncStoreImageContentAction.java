package de.benjaminborbe.vnc.service;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

import com.google.inject.Inject;

import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.image.BMPUtil;
import de.benjaminborbe.vnc.api.VncPixels;
import de.benjaminborbe.vnc.connector.VncConnector;
import de.benjaminborbe.vnc.connector.VncConnectorException;

public class VncStoreImageContentAction {

	private final VncConnector vncConnector;

	private final CalendarUtil calendarUtil;

	private final BMPUtil bmpUtil;

	private final Logger logger;

	@Inject
	public VncStoreImageContentAction(final Logger logger, final VncConnector vncConnector, final CalendarUtil calendarUtil, final BMPUtil bmpUtil) {
		this.logger = logger;
		this.vncConnector = vncConnector;
		this.calendarUtil = calendarUtil;
		this.bmpUtil = bmpUtil;
	}

	public void storeImageContent() throws VncConnectorException, IOException {
		final VncPixels vncPixels = vncConnector.getScreenContent().getPixels();
		final File file = new File("/tmp/vnc-" + calendarUtil.toDateTimeString(calendarUtil.now()) + ".bmp");
		storePixelToFile(vncPixels, file);
	}

	public void storePixelToFile(final VncPixels vncPixels, final File file) throws IOException {
		logger.debug("storePixelToFile");
		final OutputStream outputStream = new FileOutputStream(file);
		final RenderedImage image = vncConnector.getViewer().getRenderer().getBufferedImage();
		bmpUtil.writeBMP(outputStream, image, 96);
		outputStream.flush();
		outputStream.close();
	}
}