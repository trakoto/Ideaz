package com.trakoto.Ideaz.controller;

import com.trakoto.Ideaz.entity.Idea;
import com.trakoto.Ideaz.exception.RatingOutOfScaleException;
import com.trakoto.Ideaz.respository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ideas")
public class IdeaController {

    @Autowired
    public IdeaRepository ideaRepository;

    @GetMapping
    public Iterable<Idea> findIdeas() {
        return ideaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Idea> findIdea(@PathVariable Long id) {
        return ideaRepository.findById(id);
    }

    @GetMapping("/rating/{rating}")
    public List<Idea> findIdeaByRating(@PathVariable int rating) {
        if(rating > 0 && rating < 6)
            return ideaRepository.findByRating(rating);
        else
            throw new RatingOutOfScaleException("The rating must be on a scale of 1 to 5", null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Idea createNewIdea(@RequestBody Idea idea) {
        ideaRepository.save(idea);
        return idea;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Idea updateNewIdea(@RequestBody Idea idea) {
        ideaRepository.save(idea);
        return idea;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Long deleteIdea(@PathVariable Long id) {
        ideaRepository.deleteById(id);
        return id;
    }
}
