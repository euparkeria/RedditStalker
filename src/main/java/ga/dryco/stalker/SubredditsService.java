package ga.dryco.stalker;

import ga.dryco.redditJerk.controllers.Subreddit;
import ga.dryco.redditJerk.exceptions.RedditJerkException;
import ga.dryco.stalker.domain.SubredditObjects;
import ga.dryco.stalker.repositories.SubredditObjectsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class SubredditsService {

    private final Logger log = LoggerFactory.getLogger(SubredditsService.class);

    @Autowired
    private SubredditObjectsRepository repository;

    @Autowired
    private RedditJerkService rJerk;

    private Long subredditsCount;

    private Integer quaranteedCount;

    private Integer privateSubCount;

    private Integer restrictedSubCount;

    private Integer publicSubCount;

    private List<SubredditObjects> recentlyPopularSubreddits;

    public List<SubredditObjects> getRecentlyPopularSubreddits() {
        return recentlyPopularSubreddits;
    }

    public void setRecentlyPopularSubreddits(List<SubredditObjects> recentlyPopularSubreddits) {
        this.recentlyPopularSubreddits = recentlyPopularSubreddits;
    }


    public Integer getPrivateSubCount() {
        return privateSubCount;
    }

    public Integer getRestrictedSubCount() {
        return restrictedSubCount;
    }

    public Integer getPublicSubCount() {
        return publicSubCount;
    }

    //How far into the past to update records in seconds / Usually the same as the popular Last Month subreddits
    private long regularPastUpdateSeconds = (System.currentTimeMillis() / 1000L) - 2592000;

    public Integer getQuaranteedCount() {
        return quaranteedCount;
    }

    public void setQuaranteedCount(Integer quaranteedCount) {
        this.quaranteedCount = quaranteedCount;
    }

    public long getRegularPastUpdateSeconds() {
        return regularPastUpdateSeconds;
    }

    public Long getSubredditsCount() {
        if(this.subredditsCount == null) {
            this.updateSubredditsCounts();
        }
        return subredditsCount;
    }

    private void updateSubredditsCounts(){
        this.subredditsCount = repository.count();
        this.privateSubCount = repository.countBySubType("private");
        this.publicSubCount = repository.countBySubType("public");
        this.restrictedSubCount = repository.countBySubType("restricted");
        this.quaranteedCount = repository.countQuarantined();

        Integer POPULAR_SUBSCRIBERS_MINIMUM = 350;
        this.recentlyPopularSubreddits = repository.createdAfter(this.getRegularPastUpdateSeconds(), POPULAR_SUBSCRIBERS_MINIMUM, new PageRequest(0, POPULAR_SUBSCRIBERS_MINIMUM)).getContent();
        log.info("Subreddits Counts Updated.");

    }



    @Scheduled(fixedRate = 24000000)
    public void updateRecentlyCreatedSubreddits(){
        Integer MIN_SUBSCRIBERS = 0;

        this.updatePastSubreddits(this.regularPastUpdateSeconds, MIN_SUBSCRIBERS);
    }


    @Scheduled(cron = "0 15 18 * * MON")
    public void updateAllSubreddits(){
        Integer MIN_SUBSCRIBERS = 50;
        long redditCreatedDateTimestamp = 1104537600;
        this.updatePastSubreddits(redditCreatedDateTimestamp, MIN_SUBSCRIBERS);

    }

    private void updatePastSubreddits(long createdAfter, Integer minumumSubscribers){
        Integer ITEMS_PER_PAGE = 2000;


        log.info("Past Subreddits Update Scheduled task begins.");

        Pageable pageB = new PageRequest(0, ITEMS_PER_PAGE);


        do{

            Page<SubredditObjects> subObjects = this.repository.createdAfter(createdAfter, minumumSubscribers, pageB);

            log.info("Number of Subreddit Entities to update: " + subObjects.getTotalElements());

            log.info("Page" + subObjects.getNumber());
            String firstId = subObjects.getContent().get(0).getId();
            log.info("First ID: " + firstId);

            List<String> idList = this.generateIdList(firstId, subObjects.getContent().size());

            log.info("Size of Generated ID list: " + idList.size());

            this.makeInfoQueriesAndPersist(idList);

            if(subObjects.hasNext()){
                pageB = subObjects.nextPageable();
            } else pageB = null;



        }while (pageB != null);


    }


    private void makeInfoQueriesAndPersist(List<String> idList){

        Integer maxThingsPerCall = 100;
        Double numOfQueries = (idList.size() / maxThingsPerCall) + 0.5;
        Integer num = numOfQueries.intValue();
        Integer curIndex = 0;

        log.info("Queries to make: " + num);

        Integer found = 0;


        for(int i = 0; i <= num; i++){

            List<Subreddit> tmpList = new ArrayList<>();

            try{
                 tmpList = rJerk.red.getInfo_subreddit(idList.subList(curIndex, (curIndex + maxThingsPerCall) > idList.size() ? idList.size() : curIndex + maxThingsPerCall));
            }catch (RedditJerkException e){
                log.error(e.getMessage());
            }



            if(!tmpList.isEmpty()){
                this.persistSubreddits(tmpList);
                found +=  tmpList.size();

            }

            curIndex += maxThingsPerCall;

            log.info("Found so far:" + found + ", Current Index:" + curIndex);

        }


    }


    private List<String> generateIdList(String id, Integer limit){
        List<String> idList = new ArrayList<>();
        Integer subPosition = Integer.valueOf(id, 36);

        for(int i = subPosition; i<subPosition + limit; i++){

            idList.add("t5_" + Integer.toString(i, 36));

        }

        return idList;

    }

    @Scheduled(fixedRate = 600000)
    public void getNewSubreddits(){

        log.info("Get New subreddits scheduled task starts.");

        Integer checkAheadLimit = 200;

        SubredditObjects sub1 = this.repository.findFirstByOrderByCreatedUtcDesc().get(0);

        List<String> idList = this.generateIdList(sub1.getId(), checkAheadLimit);

        this.makeInfoQueriesAndPersist(idList);

        this.updateSubredditsCounts();

    }



    public void persistSubreddits(List<Subreddit> sublist){

        for(Subreddit sub:sublist){
            List<SubredditObjects> alrExists = repository.findById(sub.getId());
            SubredditObjects sbrObj;


            if(alrExists.isEmpty()) {
                sbrObj = new SubredditObjects();

            }else {
                sbrObj = alrExists.get(0);

            }

                sbrObj.setName(sub.getName());
                sbrObj.setId(sub.getId());
                sbrObj.setAccountsActive(sub.getAccountsActive());
                sbrObj.setCommentScoreHideMins(sub.getCommentScoreHideMins());
                sbrObj.setCreated(sub.getCreated());
                sbrObj.setCreatedUtc(sub.getCreatedUtc());
                sbrObj.setDescription(sub.getDescription());
                sbrObj.setDisplayName(sub.getDisplayName());
                sbrObj.setDescriptionHtml(sub.getDescriptionHtml());
                sbrObj.setHeaderImg(sub.getHeaderImg());
                sbrObj.setOver18(sub.getOver18());
                sbrObj.setUrl(sub.getUrl());
                sbrObj.setTitle(sub.getTitle());
                sbrObj.setSubmitText(sub.getSubmitText());
                sbrObj.setSubscribers(sub.getSubscribers());
                sbrObj.setSubredditType(sub.getSubredditType());
                sbrObj.setPublicDescription(sub.getPublicDescription());
                sbrObj.setSubmissionType(sub.getSubmissionType());
                sbrObj.setPublicDescriptionHtml(sub.getPublicDescriptionHtml());
                sbrObj.setQuarantine(sub.getQuarantine());


                repository.save(sbrObj);
                //log.info(sub.getDisplayName() + addOrUpdate);

            }

        }


}
