package com.learnreactiveprogramming.service;


import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.ls.LSOutput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static reactor.core.publisher.Flux.*;
@Slf4j
public class FluxAndMonoGeneratorService {
    
    /* ------------------------  ----------------------------------  -------------------------------- */
    /* ------------------------  FLUX  -------------------------------- */
    /* ------------------------  ----------------------------------  -------------------------------- */
    
    
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
    // USES FUNCTIONAL INTERFACE (TRANSFORM() OPERATOR)
    public Flux<String> namesFluxTransform(int elementLength) {
        
        // defining our own Functional Interface because transform(fi) operator takes in a Functional Interface
        // we moved the functionality of both operators(.map and .filter) and assigned it to a variable that holds their logic inside
        // *****(basically Functional Interfaces extract functionality and assigns it to a variable)
        Function <Flux<String>, Flux<String>> myFilterMap = name -> name.map(String :: toUpperCase)
                .filter(element -> element.length() > elementLength);
        
        return Flux.fromIterable(List.of("alex", "ben", "caen", "alibouz"))
                .transform(myFilterMap)
                .flatMap(s -> splitNames(s))
                .log();
    }

                            // DefaultIfEmpty (RETURNS A DEFAULT VALUE -- SINGULAR)
    //THE MAIN IDEA:
    // HERE, WE CAN FALL BACK TO A DEFAULT VALUE IF THE SOURCE OF DATA DOESN'T RETURN OR EMIT AN EVENT
    // SUCH AS ONNEXT(SOMETHING). WE CAN EMIT A DEFAULT VALUE OF THE SAME TYPE THAT WE ARE RETURNING
    public Flux<String> namesFluxTransform_DefaultIfEmpty(int elementLength) {
  
        Function <Flux<String>, Flux<String>> myFilterMap = name -> name.map(String :: toUpperCase)
                .filter(element -> element.length() > elementLength);
        
        return Flux.fromIterable(List.of("alex", "ben", "caen", "alibouz"))
                .transform(myFilterMap)
                .flatMap(s -> splitNames(s))
                .defaultIfEmpty("default")
                .log();
    }
    
                    // SwitchIfEmpty (RETURNS A DEFALUT PUBLISHER -- STREAM(MONO OR FLUX)
    // accepts a publisher (flux or a mono) to switch the stream do instead of returning the default data type like defaultifempty
                    public Flux<String> namesFluxTransform_SwitchIfEmpty(int elementLength) {
                        
                        Function <Flux<String>, Flux<String>> myFilterMap = name ->
                                name.map(String :: toUpperCase)
                                .filter(element -> element.length() > elementLength)
                                .flatMap(this::splitNames);
                        
                        
                        // this is the publisher we switch to
                        var myDefaultPublisher = Flux.just("this is the default stream - publisher");
                              //  .transform(myFilterMap);
                        
                        return Flux.fromIterable(List.of("alex", "ben", "caen", "alibouz"))
                                .transform(myFilterMap)
                                .switchIfEmpty(myDefaultPublisher)
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
    
    
    
    /* ------------------------  ----------------------------------  -------------------------------- */
    /* ------------------------  MONO  -------------------------------- */
    /* ------------------------  ----------------------------------  -------------------------------- */
    
    
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
    
    
    /*
    //////////// COMBINING DIFFERENT STREAMS INTO ONE WITH:
     CONCAT, MERGE, AND ZIP OPERATORS ////////////
     */
    
    /*
    concat() & concatWith() operators
    Example: let's say that our app is contacting two external services (APIs)
    the first API will return --> A B C
    the second API will return --> E F G
    then with the help of concat and concatWith operators, we can combine the results
    into a single stream like this --> A B C D E F G
    
    **Note: the concatination of Reactive Streams happens in a sequence
    - the first stream is subscribed first and completes
    - the second stream waits for the onComplete signal from the first then subscribed and completes
    
    -- "concat()" - static method in FLUX
    -- "concatWith()" - instance method in FLUX and MONO
    
    // both operators work similarly
     */
    
    public Flux<String> explore_concat() {
        // concat this flux with a mono stream
        var firstStream =  Flux.just("A", "B", "C").concatWith(Mono.just("D"));
        // same as first
        var secondStream = Flux.just("E", "F", "G").concatWith(Mono.just("H"));
        
        // concat both streams (fluxes) into a single flux stream
        return Flux.concat(firstStream, secondStream);
    }
    
    
    public Flux<String> explore_concat_mono() {
        
        Mono<String> firstStream = Mono.just("A");
        
        Mono<String> secondStream = Mono.just("B");
        
        // Return a Flux of both Mono streams concatination
        return firstStream.concatWith(secondStream).log();
    }
    
    
    /*
    "merge() && mergeWith()" operators
    - both the publishers are subscribed at the same time (unlike concat which is sequential)
        -- publishers are subscribed eagerly and the merge happens in an interleaved fashion (combined into one)
        
    - merge() - static method in Flux
    - mergeWith() - instance method in Flux and Mono
    
    **note: both operators work similarly
     */
    
    public Flux<String> explore_merge() {
        
        // delaying to demonstrate the simultaneous subscription to both streams
        var firstStream = Flux.just("Almuhannad", "Khalid", "Almhari")
                .delayElements(Duration.ofMillis(100));
              
        
        var secondStream = Flux.just("Ada", "Boe", "Charlie")
                .delayElements(Duration.ofMillis(125));
        
        //emitted values --> almuhannad, ada, khalid, boe, ... etc
        
        return Flux.merge(firstStream, secondStream).log();
        //similarly ...
        // OR -->  firstStream.mergeWith(secondStream);
    }
    
    public Flux<String> explore_mergeWith() {
        
        // delaying to demonstrate the simultaneous subscription to both streams
        var firstStream = Flux.just("Almuhannad", "Khalid", "Almhari")
                .delayElements(Duration.ofMillis(100));
        
        
        var secondStream = Flux.just("Ada", "Boe", "Charlie")
                .delayElements(Duration.ofMillis(125));
        
        return firstStream.mergeWith(secondStream).log();
    }
    
    // demonstrating with Mono and using the instance method mergeWith
    // ** (which works with Mono and Flux)
    public Flux<String> explore_mergeWith_mono() {
        
        // delaying to demonstrate the simultaneous subscription to both streams
        var firstStream = Mono.just("Almuhannad");
        
        var secondStream = Mono.just("Ada");
        
        return firstStream.mergeWith(secondStream).log();
    }
    
    
    /* ------------------------  ----------------------------------  -------------------------------- */
    /* ------------------------  MAIN FUNCTION FOR CONSOLE PRINTING  -------------------------------- */
    /* ------------------------  ----------------------------------  -------------------------------- */

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
                        System.out.println("Filtered name is: " + name));
      */

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

        /*fluxAndMonoGeneratorService.nameMonoWithFlatMapMany("khalid").subscribe(
                name -> System.out.println("flatMapMany returns a Flux here: " + name)
        );*/
        
        /*fluxAndMonoGeneratorService.namesFluxTransform(3).subscribe(
                name -> System.out.println("Transform returns here ::" + name)
        );*/
        
       /* fluxAndMonoGeneratorService.explore_concat().subscribe(
                result -> System.out.println("this is the result of concat --> " + result)
        );*/
        
        fluxAndMonoGeneratorService.explore_merge().subscribe(
                result -> System.out.println("here's the eager merge --> "+ result)
                );


    }




}
