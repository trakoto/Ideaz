package com.trakoto.Ideaz.respository;

import com.trakoto.Ideaz.entity.Idea;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IdeaRepository extends CrudRepository<Idea, Long> {

    List<Idea> findByRating(int rating);

}
