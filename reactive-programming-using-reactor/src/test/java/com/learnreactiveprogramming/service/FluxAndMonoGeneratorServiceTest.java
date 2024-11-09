package com.learnreactiveprogramming.service;


import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();


    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then (reactor test)
        StepVerifier.create(namesFlux)
                //.expectNext("Ahmad", "Bader", "Caeser")
                //.expectNextCount(3)
                .expectNext("Ahmad")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFluxMap() {

        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxMap();

        StepVerifier.create(namesFluxMap)
                .expectNext("IBRAHIM", "MOHAMMAD", "ISMAEL", "MOUSA")
                //.expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesFluxImmutable() {

        var namesFluxImmutable = fluxAndMonoGeneratorService.namesFluxImmutable();

        StepVerifier.create(namesFluxImmutable)
                .expectNext("aaa", "bbb", "ccc")
                //.expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void namesFluxFilter() {

        var namesFluxFilter = fluxAndMonoGeneratorService.namesFluxFilter(3);

        StepVerifier.create(namesFluxFilter)
                .expectNext("4-eddy", "6-johnny", "4-jack", "7-jameson")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {

        // testing for dorothy name just to see if it will pass and test in one element
        var namesFluxFlatMap = fluxAndMonoGeneratorService.namesFluxFlatMap(5);
        StepVerifier.create(namesFluxFlatMap)
                .expectNext("D","O","R","O","T","H","Y")
                .verifyComplete();
        // passed unit test
    }

    @Test
    void namesFluxFlatMapAsync() {
        // assuming i will receive the list of elements in order
        // but it won't be ordered! IT SHOULD FAIL
        // "aaa", "bb", "cccc"
        var namesFluxFlatMapAsync = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(1);
        StepVerifier.create(namesFluxFlatMapAsync)
                //.expectNext("A","A","A", "B","B","C","C","C","C") //this will fail (no order)
                .expectNextCount(9) // since we give only expected num of elements, it will pass
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {

        // similar to flatMap but preserves the order of stream elements
        // here, we expect the order to remain the same, so we can use .expectNext unlike flatMap
        var namesFluxConcatMap = fluxAndMonoGeneratorService.namesFluxConcatMap(2);
        // replace e with X and split names
        StepVerifier.create(namesFluxConcatMap)
                .expectNext("a","l","X", "x", "b", "X", "n", "c","a", "X", "n")
                .verifyComplete();
        /*("alex", "ben", "caen", "dorothy")*/
    }

    @Test
    void nameMonoWithFlatMap() {

        var value = fluxAndMonoGeneratorService.nameMonoWithFlatMap();
        // fixed name in method --> almuhannad
        StepVerifier.create(value)
                .expectNext(List.of("A","L","M","U","H","A", "N","N", "A", "D")) // as one unit (list of Mono)
                .verifyComplete();
    }

    @Test
    void nameMonoWithFlatMapMany() {

        var test = fluxAndMonoGeneratorService.nameMonoWithFlatMapMany("alex");

        StepVerifier.create(test)
                .expectNext("A", "L", "E", "X") // multiple emits (Flux)
                .verifyComplete();
    }
    
    @Test
    void namesFluxTransform() {
        
        var test = fluxAndMonoGeneratorService.namesFluxTransform(3);
        
        StepVerifier.create(test)
                .expectNext("A", "L", "E", "X", "C", "A", "E", "N")
                .verifyComplete();
        
    }
    
    
    @Test
    void namesFluxTransform_DefaultAndSwitchIfEmpty() {
        
        var test = fluxAndMonoGeneratorService.namesFluxTransform(9);
        
        StepVerifier.create(test)
                .expectNext("A", "L", "I", "B", "O", "U", "Z")
                .verifyComplete();
    }
    
    @Test
    void testNamesFluxTransform_DefaultIfEmpty() {
        // similar to the above test, but we already know that the condition will not be met (more than 49 chars!)
        // so, we emit the default string (our return type) which is simply "default"
        var test = fluxAndMonoGeneratorService.namesFluxTransform_DefaultIfEmpty(49);
        StepVerifier.create(test)
                .expectNext("default")
                .verifyComplete();
    }
    
    @Test
    void namesFluxTransform_SwitchIfEmpty() {
        // similar to the default if empty, but instead of returning a simple default value of the same data type of
        // our return type, we return a publisher (flux or mono) if the stream is not returning a value
        // so we switch (divert) to a different stream
        var test = fluxAndMonoGeneratorService.namesFluxTransform_SwitchIfEmpty(8);
        
        StepVerifier.create(test)
                .expectNext("this is the default stream - publisher")
                .verifyComplete();
    
    }
    
    @Test
    void explore_concat() {
        
        var test = fluxAndMonoGeneratorService.explore_concat();
        
        StepVerifier.create(test)
                .expectNext("A", "B", "C", "D", "E", "F", "G", "H")
                .verifyComplete();
        
    }
    
    @Test
    void explore_concat_mono() {
        // subscribe to first, complete, then subscribe to second and complete
        var test = fluxAndMonoGeneratorService.explore_concat_mono();
        
        StepVerifier.create(test)
                .expectNext("A", "B")
                .verifyComplete();
        
    }
    
    @Test
    void explore_merge() {
        // simultaneously subscribed to both streams
        var test = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(test)
                .expectNext("Almuhannad", "Ada", "Khalid", "Boe", "Almhari", "Charlie")
                .verifyComplete();
        
    }
    
    @Test
    void explore_mergeWith_mono() {
        
        var test = fluxAndMonoGeneratorService.explore_mergeWith_mono();
        
        StepVerifier.create(test)
                .expectNext("Almuhannad", "Ada")
                .verifyComplete();
    }
}
