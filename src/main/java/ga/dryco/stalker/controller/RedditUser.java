package ga.dryco.stalker.controller;


import ga.dryco.redditJerk.controllers.Comment;
import ga.dryco.redditJerk.controllers.Link;

import ga.dryco.redditJerk.controllers.User;
import ga.dryco.redditJerk.datamodels.PostData;

import ga.dryco.stalker.RedditJerkService;

import ga.dryco.stalker.domain.Redditor;
import ga.dryco.stalker.domain.srsusers;
import ga.dryco.stalker.exception.RedditorNotFoundException;
import ga.dryco.stalker.repositories.SrsusersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class RedditUser {

    @Autowired
    public RedditJerkService rJerk;

    @Autowired
    SrsusersRepository srsReposityry;

    @RequestMapping(value = "/RedditUser/{credditor}", method= RequestMethod.GET)
    public Redditor redditorView(@PathVariable String credditor){

        User user1;
        List<Link> sbmList;
        List<Comment> cmnList;
        Redditor returnObj = new Redditor();
        Map<String, Integer> hourMap = new HashMap<>();

        //API call to get the User Object
        user1 = rJerk.red.getUser(credditor);
        if(user1 == null) throw new RedditorNotFoundException("User object is null when trying to get user:" + credditor);
        returnObj.setUserData(user1);


        //API call to get the last 1000 user's submitted Links
        sbmList =  user1.getSubmitted(2000);

        //API call to get the last 1000 user's submitted Comments
        cmnList = user1.getComments(2000);
        if(cmnList != null){
            returnObj.setCommentsData(cmnList);
            returnObj.setCommentedInSubreddits(this.sortAndAggregateBySubreddit(cmnList));
            this.agrregateHoursOfPosts(cmnList, hourMap);
        }

        if(sbmList != null) {
            returnObj.setSubmittionsData(sbmList);
            returnObj.setSubmittedInSubreddits(this.sortAndAggregateBySubreddit(sbmList));
            this.agrregateHoursOfPosts(sbmList, hourMap);

        }

        if(!hourMap.isEmpty()){

            Map<String, Integer> treeMapc = new TreeMap<>(
                    new Comparator<String>() {
                        DateFormat sdf = new SimpleDateFormat("hh:mm a");

                        @Override
                        public int compare(String o1, String o2) {
                            Date date1 = null;
                            Date date2 = null;
                            try {
                                date1 = sdf.parse(o1);
                                date2 = sdf.parse(o2);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return date1.compareTo(date2);
                        }

                    });

            treeMapc.putAll(hourMap);

            returnObj.setPostHoursAggr(treeMapc);
        }


        List<srsusers> brigadier = srsReposityry.findByUsername(user1.getName());
        if(!brigadier.isEmpty()){
            returnObj.setKnownBrigadier(true);
        }

        return returnObj;

    }

    private <T extends PostData> List<Redditor.SubredditCountData> sortAndAggregateBySubreddit(List<T> postList){

            List<Redditor.SubredditCountData> subrSubmList = new ArrayList<>();
            for(T sbr: postList){
                Boolean found = false;
                for(Redditor.SubredditCountData subObjlist: subrSubmList){
                    if(subObjlist.subreddit.equals(sbr.getSubreddit())){
                        found = true;
                        subObjlist.setCount(subObjlist.getCount() + 1);
                        subObjlist.setKarmaBalance(subObjlist.getKarmaBalance() + sbr.getScore());
                    }
                }
                if(!found){
                    subrSubmList.add(new Redditor.SubredditCountData(sbr.getSubreddit(), 1, sbr.getScore()));
                }
            }
            return subrSubmList;
        }

    private <T extends PostData> Map<String, Integer> agrregateHoursOfPosts(List<T> postList, Map<String, Integer> hourAggr){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:'00' a");

        for(T pst: postList){

            calendar.setTimeInMillis(pst.getCreatedUtc() * 1000);


            String hour = sdf.format(calendar.getTime());


            Integer value = hourAggr.get(hour);
            if(value == null){
                hourAggr.put(hour, 1);
            }else {
                hourAggr.put(hour, value + 1);
            }

        }

        return hourAggr;


    }


}
