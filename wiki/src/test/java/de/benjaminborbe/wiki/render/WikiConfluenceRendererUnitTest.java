package de.benjaminborbe.wiki.render;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.benjaminborbe.wiki.render.WikiConfluenceRenderer;

public class WikiConfluenceRendererUnitTest {

	@Test
	public void testRener() {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("", converter.render(""));
		assertEquals("bla", converter.render("bla"));
	}

	@Test
	public void testRenderLi() {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("<ul><li>l1</li></ul>", converter.render("* l1"));
		assertEquals("<ul><li>l1</li></ul>", converter.render("* l1\n"));
		assertEquals("<ul><li>l1</li><li>l2</li></ul>", converter.render("* l1\n* l2"));
		assertEquals("<ul><li>l1</li><li>l2</li></ul>", converter.render("* l1\n* l2\n"));
		assertEquals("<ul><li>l1</li><li>l2</li><li>l3</li></ul>", converter.render("* l1\n* l2\n* l3"));
		assertEquals("<ul><li>l1</li><li>l2</li><li>l3</li></ul>", converter.render("* l1\n* l2\n* l3\n"));
	}

	@Test
	public void testRenderToc() throws Exception {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("", converter.render("{toc}"));
	}

	@Test
	public void testRenderH1() throws Exception {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("<h1><a name=\"head1\"></a>head1</h1>", converter.render("h1. head1"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1>", converter.render("H1. head1"));
	}

	@Test
	public void testRenderH2() throws Exception {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("<h2><a name=\"none-head2\"></a>head2</h2>", converter.render("h2. head2"));
		assertEquals("<h2><a name=\"none-head2\"></a>head2</h2>", converter.render("H2. head2"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2>", converter.render("h1. head1\nh2. head2"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2>", converter.render("H1. head1\nH2. head2"));
	}

	@Test
	public void testRenderH3() throws Exception {
		final WikiRenderer converter = new WikiConfluenceRenderer();
		assertEquals("<h3><a name=\"none-none-head3\"></a>head3</h3>", converter.render("h3. head3"));
		assertEquals("<h3><a name=\"none-none-head3\"></a>head3</h3>", converter.render("H3. head3"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2>", converter.render("h1. head1\nh2. head2"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2>", converter.render("H1. head1\nH2. head2"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2><h3><a name=\"head1-head2-head3\"></a>head3</h3>",
				converter.render("h1. head1\nh2. head2\nh3. head3"));
		assertEquals("<h1><a name=\"head1\"></a>head1</h1><h2><a name=\"head1-head2\"></a>head2</h2><h3><a name=\"head1-head2-head3\"></a>head3</h3>",
				converter.render("H1. head1\nH2. head2\nH3. head3"));
	}

}
