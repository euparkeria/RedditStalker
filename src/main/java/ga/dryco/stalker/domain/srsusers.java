package ga.dryco.stalker.domain;
import com.sun.istack.internal.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class srsusers {

    @Id
    @NotNull
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @NotNull
    private String username;

    private String subreddit;

    private String reddit_id;

    private String last_check_date;

    private Integer srs_karma_balance;

    private Integer invasion_number;

    protected srsusers() {

    }

    public srsusers(String username, String subreddit, String reddit_id, String last_check_date, Integer srs_karma_balance, Integer invasion_number) {
        this.username = username;
        this.subreddit = subreddit;
        this.reddit_id = reddit_id;
        this.last_check_date = last_check_date;
        this.srs_karma_balance = srs_karma_balance;
        this.invasion_number = invasion_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getReddit_id() {
        return reddit_id;
    }

    public void setReddit_id(String reddit_id) {
        this.reddit_id = reddit_id;
    }

    public String getLast_check_date() {
        return last_check_date;
    }

    public void setLast_check_date(String last_check_date) {
        this.last_check_date = last_check_date;
    }

    public Integer getSrs_karma_balance() {
        return srs_karma_balance;
    }

    public void setSrs_karma_balance(Integer srs_karma_balance) {
        this.srs_karma_balance = srs_karma_balance;
    }

    public Integer getInvasion_number() {
        return invasion_number;
    }

    public void setInvasion_number(Integer invasion_number) {
        this.invasion_number = invasion_number;
    }

    public long getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }
}

