package com.learnreactiveprogramming.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static reactor.core.publisher.Flux.fromArray;

public class FluxAndMonoGeneratorService {


    // Flux
    public Flux<String> namesFlux() {

        return Flux.fromIterable(List.of("Alex", "Ben", "Caeser")) // might come from a db or a remote service call
                .log(); // log to show the flow of events like request(), onNext(), onComplete() like diagrams

    }

    // using map operator to apply transformations
    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("ibrahim", "mohammad", "ismael", "mousa"))
                .map(name -> name.toUpperCase())
                .log();
    }

    // reactive streams are immutable (taking from namesFlux, storing, then trying to change values of stream)
    public Flux<String> namesFluxImmutable() {

        var names = Flux.fromIterable(List.of("aaa", "bbb", "ccc"));

        // if we return here, the stream will be transformed to upper case
        names.map(name -> name.toUpperCase());

        // if we return here, nothing will happen to the stream as changes will be dropped
        // and no transformations will be applied (immutable streams)
        return names;
    }


    // filter operator used to filter elements of stream based on condition (predicate)
    public Flux<String> namesFluxFilter(int elementLength) {

        var filteredNames = Flux.fromIterable(List.of("ed", "edd", "eddy", "johnny", "jack", "jameson"));

        // combining filter with map to get a better result
        // chaining multiple operators is called (pipeline) in reactive streams
        return filteredNames.filter(name -> name.length() > elementLength)
                .map(name -> name.length() + "-" + name);
    }

    // FLATMAP OPERATOR
    // the flatmap operator transforms one source element to a Flux of 1 to N elements
    // example: "ALEX" -> Flux.just("A","L","E","X")
    // use it when the transformation returns a Reactive type (Mono or Flux)
    // always return a Flux of a given type: Flux<Type>

    public Flux<String> namesFluxFlatMap(int elementLength) {
        return Flux.fromIterable(List.of("alex", "ben", "caen", "dorothy", "egor"))
                .map(element -> element.toUpperCase())
                .filter(name -> name.length() > elementLength)
                // or use the helper method for cleaner code. here we make each element
                // a stream of string characters
                //.flatMap(name -> fromArray(name.split("")))
                .flatMap(name -> splitNames(name))
                .log();
    }

    // helper function to split string objects to return each character as a stream
    public Flux<String> splitNames(String name) {

        // this .split string method will split the individual characters in a string
        // we need to leave the method input as an empty string because we don't
        // wish to provide a regex or complex predicate for splitting the chars
        String[] nameArray = name.split("");
        return fromArray(nameArray);
    }


    //Mono
    public Mono<String> nameMono() {
        return Mono.just("Almuhannad").log();
    }



    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

      /*  fluxAndMonoGeneratorService.namesFlux() // nothing will happen until we subscribe
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
                        System.out.println("Filtered name is: " + name));*/

        // flatmap
        fluxAndMonoGeneratorService.namesFluxFlatMap(5).subscribe(
                name -> System.out.println("FLATMAP OUTPUT HERE:" + name));

    }




}
