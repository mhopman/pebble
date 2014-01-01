/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTagsTest extends AbstractTest {

	@Test
	public void testBlock() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.grandfather.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testIf() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if false or steve == true  %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("yes", true);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testFor() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{% if loop.index == 0 %}[{{ loop.length }}]{% endif %}{{ loop.index }}{{ user.username }}{% endfor %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("[2]0Alex1Bob", writer.toString());
	}

	@Test
	public void multipleForLoops() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "" + "{% for user in users %}{% endfor %}" + "{% for user in users %}{% endfor %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
	}

	@Test
	public void testForElse() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{{ user.username }}{% else %}yes{% endfor %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testMacro() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.macro1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n"
				+ "	<input name=\"company\" value=\"forcorp\" type=\"text\" data-overload=\"overloaded\"/>\n",
				writer.toString());
	}

	@Test
	public void testMacroFromAnotherFile() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.macro2.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", writer.toString());
	}

	@Test
	public void testInclude() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.include1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TEMPLATE2\nTEMPLATE1\nTEMPLATE2\n", writer.toString());
	}

	@Test
	public void testSet() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set name = 'alex'  %}{{ name }}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "steve");

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("alex", writer.toString());
	}

	public class User {
		private final String username;

		public User(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}
	}
}