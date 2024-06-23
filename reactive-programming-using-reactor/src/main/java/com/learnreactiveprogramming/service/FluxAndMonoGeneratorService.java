package com.learnreactiveprogramming.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {


    // Flux
    public Flux<String> namesFlux() {

        return Flux.fromIterable(List.of("Alex", "Ben", "Caeser")) // might come from a db or a remote service call
                .log(); // log to show the flow of events like request(), onNext(), onComplete() like diagrams

    }

    // using map operator to apply transformations
    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("ibrahim", "mohammad", "ismael", "mousa")).map(name ->
                name.toUpperCase()).log();
    }

    // reactive streams are immutable (taking from namesFlux, storing, then trying to change values of stream)
    public Flux<String> namesFluxImmutable() {

        var names = Flux.fromIterable(List.of("aaa", "bbb", "ccc"));

        // if i return here, the stream will be transformed to upper case
        names.map(name ->
                name.toUpperCase());

        // if i return here, nothing will happen to the stream as changes will be dropped
        // and no transformations will be applied (immutable streams)
        return names;
    }


    // filter operator used to filter elements of stream based on condition (predicate)
    public Flux<String> namesFluxFilter(int elementLength) {

        var filteredNames = Flux.fromIterable(List.of("ed", "edd", "eddy", "johnny", "jack", "jameson"));

        // combining filter with map to get a better result
        // chaining multiple operators is called (pipeline) in reactive streams
        return filteredNames.filter(name ->
                name.length() > elementLength)
                .map(name ->
                        name.length() + "-" + name);
    }



    //Mono
    public Mono<String> nameMono() {
        return Mono.just("Almuhannad").log();
    }



    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux() // nothing will happen until we subscribe
                .subscribe(name ->
                        System.out.println("Flux name is: " + name)); // once subbed, a stream will be sent to be consumed


        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name ->
                        System.out.println("Mono name is: "+ name));

        fluxAndMonoGeneratorService.namesFluxMap()
                .subscribe(name ->
                        System.out.println("Capitalized name is: " + name));


        fluxAndMonoGeneratorService.namesFluxImmutable()
                .subscribe(name ->
                        System.out.println("Immutable name is: " + name));

        fluxAndMonoGeneratorService.namesFluxFilter(3)
                .subscribe(name ->
                        System.out.println("Filtered name is: " + name));


    }




}
