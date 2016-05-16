package ga.dryco.stalker.controller;


import ga.dryco.stalker.SubredditsService;
import ga.dryco.stalker.domain.SubredditObjects;
import ga.dryco.stalker.repositories.SubredditObjectsRepository;
import ga.dryco.stalker.domain.SubredditsStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListSubreddits {

    @Autowired
    private SubredditObjectsRepository repository;

    @Autowired
    private SubredditsService subService;



    @RequestMapping(value="/listSubreddits", params = {"count", "page"}, method= RequestMethod.GET)
    public Page<SubredditObjects> listSubreddits(@RequestParam("count") Integer count, @RequestParam("page") Integer page) {

        return repository.findAll(new PageRequest(page - 1, count));

    }

    @RequestMapping(value = "/subredditsStats" , method = RequestMethod.GET)
    public SubredditsStats subredditsStats(){

        SubredditsStats substats = new SubredditsStats();


        //we dont get the count from the repository because it's slow,
        // istead we get a pregenerated count from the service
        substats.setSubredditCount(subService.getSubredditsCount());
        substats.setPrivateSubCount(subService.getPrivateSubCount());
        substats.setPublicSubCount(subService.getPublicSubCount());
        substats.setRestrictedSubCount(subService.getRestrictedSubCount());
        substats.setQuaranteedCount(subService.getQuaranteedCount());
        substats.setTop100Subreddits(repository.findTopSubreddits());

        substats.setSubreddits(subService.getRecentlyPopularSubreddits());


        return substats;


    }


}
