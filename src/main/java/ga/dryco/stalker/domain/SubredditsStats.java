package ga.dryco.stalker.domain;


import java.util.List;

public class SubredditsStats {

    private List<SubredditObjects> subreddits;

    private List<SubredditObjects> top100Subreddits;

    private Long SubredditCount;

    private Integer privateSubCount;

    private Integer restrictedSubCount;

    private Integer publicSubCount;

    private Integer quaranteedCount;


    public List<SubredditObjects> getTop100Subreddits() {
        return top100Subreddits;
    }

    public void setTop100Subreddits(List<SubredditObjects> top100Subreddits) {
        this.top100Subreddits = top100Subreddits;
    }

    public Integer getQuaranteedCount() {
        return quaranteedCount;
    }

    public void setQuaranteedCount(Integer quaranteedCount) {
        this.quaranteedCount = quaranteedCount;
    }

    public Integer getPrivateSubCount() {
        return privateSubCount;
    }

    public void setPrivateSubCount(Integer privateSubCount) {
        this.privateSubCount = privateSubCount;
    }

    public Integer getRestrictedSubCount() {
        return restrictedSubCount;
    }

    public void setRestrictedSubCount(Integer restrictedSubCount) {
        this.restrictedSubCount = restrictedSubCount;
    }

    public Integer getPublicSubCount() {
        return publicSubCount;
    }

    public void setPublicSubCount(Integer publicSubCount) {
        this.publicSubCount = publicSubCount;
    }

    public List<SubredditObjects> getSubreddits() {
        return subreddits;
    }

    public void setSubreddits(List<SubredditObjects> subreddits) {
        this.subreddits = subreddits;
    }

    public Long getSubredditCount() {
        return SubredditCount;
    }

    public void setSubredditCount(Long subredditCount) {
        SubredditCount = subredditCount;
    }

    public SubredditsStats() {

    }



}
