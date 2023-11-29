package com.example.apollotabacco.services;

import com.example.apollotabacco.entities.AnimalPair;
import com.example.apollotabacco.entities.AnimalRelationship;
import com.example.apollotabacco.entities.User;
import com.example.apollotabacco.repositories.AnimalRelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnimalRelationshipService {
    @Autowired
    private AnimalRelRepo animalRelRepo;
    @Autowired
    private UserService userService;
    public AnimalRelationship save(List <AnimalRelationship> relationship) {
        relationship.forEach(x -> {
            AnimalRelationship animalRelationship = animalRelRepo.findByAnimalId1AndAnimalId2(x.getAnimalId1(), x.getAnimalId2());
            if(animalRelationship != null) {
                animalRelationship.setCount(animalRelationship.getCount() + 1);
                animalRelRepo.save(animalRelationship);
            }
            else {
                x.setCount(1);
                animalRelRepo.save(x);
            }
        });
        return null;
    }
    public List<AnimalRelationship> saveAll(List<AnimalRelationship> list) {
        return animalRelRepo.saveAll(list);
    }
    public void generateAnimals() {
        List<AnimalRelationship> list =  animalRelRepo.findAll();
        Map<AnimalPair, Integer> connectionCount = new HashMap<>();
        for (AnimalRelationship connection : list) {
            AnimalPair pair = new AnimalPair(connection.getAnimalId1(), connection.getAnimalId2());
            connectionCount.put(pair, connectionCount.getOrDefault(pair, 0) + connection.getCount());
        }

        list.sort((c1, c2) -> connectionCount.get(new AnimalPair(c2.getAnimalId1(), c2.getAnimalId2())) -
                connectionCount.get(new AnimalPair(c1.getAnimalId1(), c1.getAnimalId2())));


        Map<Long, Long> bestPartners = new HashMap<>();
        for (AnimalRelationship connection : list) {
            Long animal1 = connection.getAnimalId1();
            Long animal2 = connection.getAnimalId2();
            System.out.println(animal1 + " " + animal2);
            System.out.println(bestPartners.containsKey(animal1) && bestPartners.containsValue(animal1) && bestPartners.containsKey(animal2) && bestPartners.containsValue(animal2));
            if (bestPartners.containsKey(animal1) && bestPartners.get(animal1).equals(animal2) &&
                    bestPartners.containsKey(animal2) && bestPartners.get(animal2).equals(animal1)) {
                continue;
            }
            if (!bestPartners.containsKey(animal1)) {
                bestPartners.put(animal1, animal2);
            }
        }
        System.out.println(bestPartners);
        int i = 1;
        for (Long animal : bestPartners.keySet()) {
            Long partner = bestPartners.get(animal);
            User user1 = userService.findById(animal);
            User user2 = userService.findById(partner);
            user1.setPhoneNumber(i);
            user2.setPhoneNumber(i);
            i++;
            System.out.println(user1);
            System.out.println(user2);
            user1.setIsDeleted("false");
            user2.setIsDeleted("false");
            userService.save(user1);
            userService.save(user2);
        }

    }
}
