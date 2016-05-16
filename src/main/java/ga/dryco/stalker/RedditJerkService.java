package ga.dryco.stalker;

import ga.dryco.redditJerk.Reddit;
import ga.dryco.redditJerk.RedditApi;

import ga.dryco.redditJerk.controllers.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Initializing the RedditJerk instance
 */
@Service
@Scope("singleton")
public class RedditJerkService {

    private final Logger log = LoggerFactory.getLogger(RedditJerkService.class);
    public Reddit red = RedditApi.getRedditInstance("RedditStalker0.2");

    public RedditJerkService(){
        //this.siteAccount = this.rLogin();
    }


    public User rLogin() {
        User suser = red.login("RedditJerkTest", "jerkjerkjerk", "WoXLiKdjulE09Q", "QoG2unmpgAum-IQ92NDhhNy-UKs");
        log.info("Authenticated as: " + suser.getName());
        return suser;

    }
}
