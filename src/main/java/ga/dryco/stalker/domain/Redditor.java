package ga.dryco.stalker.domain;

import ga.dryco.redditJerk.controllers.Comment;
import ga.dryco.redditJerk.controllers.Link;
import ga.dryco.redditJerk.controllers.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Redditor {

    private List<SubredditCountData> SubmittedInSubreddits = new ArrayList<>();
    private List<SubredditCountData> CommentedInSubreddits = new ArrayList<>();
    private User userData;
    private List<Link> submittionsData;
    private List<Comment> commentsData;
    private Boolean knownBrigadier;

    public Boolean getKnownBrigadier() {
        return knownBrigadier;
    }

    public void setKnownBrigadier(Boolean knownBrigadier) {
        this.knownBrigadier = knownBrigadier;
    }

    public void setPostHoursAggr(Map<String, Integer> postHoursAggr) {
        this.postHoursAggr = postHoursAggr;
    }
    private Map<String, Integer> postHoursAggr;


    public Map<String, Integer> getPostHoursAggr() {
        return postHoursAggr;
    }


    public List<SubredditCountData> getCommentedInSubreddits() {
        return CommentedInSubreddits;
    }

    public void setCommentedInSubreddits(List<SubredditCountData> commentedInSubreddits) {
        CommentedInSubreddits = commentedInSubreddits;
    }

    public List<Comment> getCommentsData() {
        return commentsData;
    }

    public void setCommentsData(List<Comment> commentsData) {
        this.commentsData = commentsData;
    }

    public List<Link> getSubmittionsData() {
        return submittionsData;
    }

    public void setSubmittionsData(List<Link> submittionsData) {
        this.submittionsData = submittionsData;
    }

    public User getUserData() {
        return userData;
    }

    public List<SubredditCountData> getSubmittedInSubreddits() {
        return SubmittedInSubreddits;
    }

    public void setSubmittedInSubreddits(List<SubredditCountData> submittedInSubreddits) {
        SubmittedInSubreddits = submittedInSubreddits;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }



    public final static class SubredditCountData {
        public String subreddit;
        public Integer count;
        public Integer KarmaBalance;


        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getKarmaBalance() {
            return KarmaBalance;
        }

        public void setKarmaBalance(Integer karmaBalance) {
            KarmaBalance = karmaBalance;
        }



        public SubredditCountData(String dsubreddit, Integer dcount, Integer dkarma){
            this.subreddit = dsubreddit;
            this.count = dcount;
            this.KarmaBalance = dkarma;
        }
    }


    public Redditor(){

    }



}
