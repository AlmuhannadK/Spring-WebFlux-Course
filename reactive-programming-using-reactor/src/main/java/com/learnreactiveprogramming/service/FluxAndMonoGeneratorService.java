package com.learnreactiveprogramming.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {


    // Flux
    public Flux<String> namesFlux() {

        return Flux.fromIterable(List.of("Ahmad", "Bader", "Caeser")); // might come from a db or a remote service call

    }

    //Mono
    public Mono<String> nameMono() {
        return Mono.just("Almuhannad");
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux() // nothing will happen until we subscribe
                .subscribe(name ->
                        System.out.println("Flux name is: " + name)); // once subbed, a stream will be sent to be consumed


        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name ->
                        System.out.println("Mono name is: "+ name));

    }



}
