package com.learnreactiveprogramming.service;


import org.w3c.dom.ls.LSOutput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import static reactor.core.publisher.Flux.fromArray;
import static reactor.core.publisher.Flux.never;

public class FluxAndMonoGeneratorService {


    /* ------------------------  FLUX  -------------------------------- */

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


    // concatMap is similar to flatMap, but it PRESERVES the order of elements unlike flatMap (keeps same order)
    // overall time will be higher than the flatmap operator because it keeps the order and needs each element
    // to complete before moving to the next one (trade-off, slower but keeps order intact)
    public Flux<String> namesFluxConcatMap(int elementLength) {

        return Flux.fromIterable(List.of("alex", "ben", "caen"))
                .map(name -> name.replace('e', 'X'))
                .filter(element -> element.length() > elementLength)
                .concatMap(s -> splitNamesWithDelay(s))
                .log();
    }


                    /*HELPER FUNCTIONS FOR FLUX PART*/

    // helper function to split string objects to return each character as a stream
    public Flux<String> splitNames(String name) {

        // this .split string method will split the individual characters in a string
        // we need to leave the method input as an empty string because we don't
        // wish to provide a regex or complex predicate for splitting the chars
        String[] nameArray = name.split("");
        return fromArray(nameArray);
    }

    // ASYNC NATURE OF FLATMAP OPERATOR
    // the flatmap operator works in async by default, so you have to keep that in mind
    // for example, if you had a list ("aa", "bbb", "cccc")
    // the flatmap operator will retrieve the streams of each element asynchronously
    // and the order may be random like we might get the c's before a's and so on
    // so keep that in mind next time you use flatmap operator and avoid it if order matters.
    public Flux<String> namesFluxFlatMapAsync(int elementLength) {
        return Flux.fromIterable(List.of("aaa", "bb", "cccc"))
                .map(element -> element.toUpperCase())
                .filter(name -> name.length() > elementLength)
                .flatMap(name -> splitNamesWithDelay(name))
                .log();
    }

    //async helper function (add artificial delay to showcase async)
    public Flux<String> splitNamesWithDelay(String name) {

        String[] nameArray = name.split("");
        //random value of delay
        var delay = new Random().nextInt(2000);
        return fromArray(nameArray)
                .delayElements(Duration.ofMillis(delay));
    }




    /* ------------------------  MONO  -------------------------------- */

    public Mono<String> nameMono() {
        return Mono.just("almuhannad")
                .log();
    }

    public Mono<String> nameMonoWithMap(String name) {
        return Mono.just(name)
                .map(element -> element.toUpperCase())
                .log();
    }


    // flatmap in Mono
    // used when the transformation returns a Mono -- (1)
    // for example, when you have a function that takes a String (1)  and returns a Mono<List<String>> (1 -- one list of <N> Strings, but treated as 1 Mono)
    public Mono<List<String>> nameMonoWithFlatMap() {
        return Mono.just("almuhannad")
                .map(name -> name.toUpperCase())
                .flatMap(s -> splitStringMono(s))
                .log();

    }


    // flatMapMany operator
    /*
    * works similarly to flatMap, but used when transformation returns a Flux (N)
    * for example, when you have a function that takes a String (1) and return a Flux<String> (N)
    * remember, flatMapMany is a function that belongs to Mono, so that's why we are emitting a Mono here that returns a Flux<String>
    * */
    public Flux<String> nameMonoWithFlatMapMany(String name) {
        return Mono.just(name)
                .map(element -> element.toUpperCase())
                /*.flatMapMany(element -> {
                    String[] nameArray = element.split("");
                    return fromArray(nameArray);
                }) */
                .flatMapMany(s -> splitNames(s)) // helper function defined in Flux section that returns a Flux<String>
                .log();
    }





    // HELPER METHODS
    public Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        return Mono.just(List.of(charArray))
                .delayElement(Duration.ofSeconds(1));
    }


    /* ------------------------  MAIN FUNCTION FOR CONSOLE PRINTING  -------------------------------- */

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
        /*fluxAndMonoGeneratorService.namesFluxFlatMap(5).subscribe(
                name -> System.out.println("FLATMAP OUTPUT HERE: " + name));*/

        /*fluxAndMonoGeneratorService.namesFluxFlatMapAsync(1).subscribe(
                name -> System.out.println("DELAYED FLATMAP OUTPUT HERE: " + name));*/

        // concatMap
        /*fluxAndMonoGeneratorService.namesFluxConcatMap(2).subscribe(
                name -> System.out.println("concatMap with (same order): " + name)
        );*/

       /* fluxAndMonoGeneratorService.nameMonoWithMap("alexander").subscribe(
                name -> System.out.println("Mono with Map: " + name)
        );*/

        /*
        resorted to blocking the thread to print the result to console
        The block() method is used to wait for the Mono to complete and retrieve its value.
        This ensures that the main method waits for the asynchronous operation to complete.
        */
        /*List<String> printResult = fluxAndMonoGeneratorService.nameMonoWithFlatMap().block();
        if (printResult != null) {
            printResult.forEach(System.out::println);
        }*/

        fluxAndMonoGeneratorService.nameMonoWithFlatMapMany("khalid").subscribe(
                name -> System.out.println("flatMapMany returns a Flux here: " + name)
        );


    }




}
