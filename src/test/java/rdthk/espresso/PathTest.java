package rdthk.espresso;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {
    @Test
    void testEmptyPath() {
        Path path = new Path("");
        assertTrue(path.match("").matches);
        assertFalse(path.match("foo").matches);
    }

    @Test
    void testSimplePath() {
        Path path = new Path("foo");
        assertTrue(path.match("foo").matches);
        assertFalse(path.match("bar").matches);
    }

    @Test
    void testParameterPathMatchesEmptyRoute() {
        Path path = new Path(":foo");
        assertTrue(path.match("").matches);
    }

    @Test
    void testParameterPathMatchesNonEmptyRoute() {
        Path path = new Path(":foo");
        assertTrue(path.match("bar").matches);
    }

    @Test
    void testMultipleParametersMatch() {
        Path path = new Path(":foo/:bar");
        assertTrue(path.match("foo/bar").matches);
    }

    @Test
    void testEmptyParameterValue() {
        Path path = new Path(":foo");
        assertTrue(path.match("").parameters.get("foo").isEmpty());
    }

    @Test
    void testSingleParameterValue() {
        Path path = new Path(":foo");
        assertEquals("foo", path.match("foo").parameters.get("foo"));
    }

    @Test
    void testComplexPath() {
        Path path = new Path("/:foo/:bar/");
        Path.MatchResult result = path.match("/1/2/");
        assertTrue(result.matches);
        assertEquals("1", result.parameters.get("foo"));
        assertEquals("2", result.parameters.get("bar"));
    }

    @Test
    void testEscapeRegex() {
        Path path = new Path("?+[{|()^$.");
        assertTrue(path.match("?+[{|()^$.").matches);
    }

    @Test
    void testWildcard() {
        Path path = new Path("*");
        assertTrue(path.match("Hello").matches);
    }

    @Test
    void testReturnsMatchedString() {
        Path path = new Path("*");
        assertEquals("Hello", path.match("Hello").text);
    }
}
