package ga.dryco.stalker.controller;



import ga.dryco.stalker.repositories.SrsusersRepository;

import ga.dryco.stalker.domain.srsusers;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ListSrsUsers {

    @Autowired
    private SrsusersRepository repository;


    @RequestMapping(value="/srs", params = {"count", "page"}, method=RequestMethod.GET)
    public Page<srsusers> listAllSrs(@RequestParam("count") Integer count, @RequestParam("page") Integer page) {

        return repository.findAll(new PageRequest(page - 1, count));

    }




}