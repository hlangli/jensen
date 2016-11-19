package dk.langli.jensen;

import java.util.Collection;

public class DefaultSecurityFilter implements SecurityFilter {
    public enum MatchType { EQUALS, STARTS_WITH, REGEX };
    private String[] allowMatches;
    private MatchType matchType;
    
    public DefaultSecurityFilter(MatchType matchType, Collection<String> allowMatches) {
        this.matchType = matchType;
        this.allowMatches = allowMatches.toArray(new String[allowMatches.size()]);
    }

    @Override
    public boolean isAllowed(Request request) {
        boolean allowed = true;
        if(allowMatches.length > 0) {
            allowed = false;
            for(int i=0; !allowed && i<allowMatches.length; i++) {
                String allowMatch = allowMatches[i];
                switch(matchType) {
                    case EQUALS:
                        if(request.getMethod().equals(allowMatch)) {
                            allowed = true;
                        }
                        break;
                    case STARTS_WITH:
                        if(request.getMethod().startsWith(allowMatch)) {
                            allowed = true;
                        }
                        break;
                    case REGEX:
                        if(request.getMethod().matches(allowMatch)) {
                            allowed = true;
                        }
                        break;
                }
            }
        }
        return allowed;
    }
}
