package rdthk.espresso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    private static final Pattern PARAM_PATTERN = Pattern.compile(":([a-zA-Z_][a-zA-Z0-9_]*)");
    private final Pattern pattern;
    private final List<String> parameterNames = new ArrayList<>();

    public Path(String pattern) {
        this.pattern = compile(pattern);
    }

    public MatchResult match(String path) {
        Matcher matcher = pattern.matcher(path);
        boolean matches = matcher.matches();
        String text = "";

        if (matches) {
            text = matcher.group();
        }

        Map<String, String> parameterValues = new HashMap<>();

        for (String name : parameterNames) {
            parameterValues.put(name, matcher.group(name));
        }

        return new MatchResult(matches, text, parameterValues);
    }

    private Pattern compile(String pattern) {
        String escaped = pattern.replaceAll("([.?+\\[{|()^$])", "\\\\$1");
        String expandWildcard = escaped.replaceAll("\\*", ".*");
        Matcher matcher = PARAM_PATTERN.matcher(expandWildcard);

        while (matcher.find()) {
            parameterNames.add(matcher.group(1));
        }

        return Pattern.compile(matcher.replaceAll("(?<$1>.*)"));
    }

    public static class MatchResult {
        public final boolean matches;
        public final String text;
        public final Map<String, String> parameters;

        private MatchResult(boolean matches, String text, Map<String, String> parameters) {
            this.matches = matches;
            this.text = text;
            this.parameters = parameters;
        }
    }
}
